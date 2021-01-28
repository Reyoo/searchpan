package com.libbytian.pan.crawler.service.aidianying;

import cn.hutool.core.util.StrUtil;
import com.libbytian.pan.proxy.service.FindFishUrlConnection;
import com.libbytian.pan.proxy.service.GetProxyService;
import com.libbytian.pan.proxy.service.PhantomJsProxyCallService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import com.libbytian.pan.system.service.impl.InvalidUrlCheckingService;
import com.libbytian.pan.system.util.UserAgentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @ProjectName: pan
 * @Package: com.search.pan.system.service
 * @ClassName: AiDianyingService
 * @Author: sun71
 * @Description: 爱电影爬虫获取
 * @Date: 2020/8/30 16:19
 * @Version: 1.0
 */

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AiDianyingService {


    private final RedisTemplate redisTemplate;
    private final InvalidUrlCheckingService invalidUrlCheckingService;
    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final PhantomJsProxyCallService phantomJsProxyCallService;


    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;


    public Set<String> firstFindLxxhUrl(String searchMovieName, String proxyIpAndPort) {


        Set<String> movieUrlInLxxh = new HashSet();
        String encode = null;
        try {
            encode = URLEncoder.encode(searchMovieName, "UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(encode);
        String urlAiDianying = lxxhUrl + "/?s=" + encode;
        //采用phantomJs 无界浏览器形式访问


        PhantomJSDriver firstBrowserDriver = phantomJsProxyCallService.create(urlAiDianying, proxyIpAndPort);
        System.out.println(firstBrowserDriver.getPageSource());
        Document document = Jsoup.parse(firstBrowserDriver.getPageSource());
        //如果未找到，放弃爬取，直接返回
        if (document.getElementsByClass("entry-title").text().equals("未找到")) {
            log.info("----------------爱电影网站未找到-> " + searchMovieName + " <-放弃爬取---------------");
            return movieUrlInLxxh;
        }
        //解析h2 标签 如果有herf 则取出来,否者 直接获取百度盘
        Elements attr = document.getElementsByTag("h2").select("a");
        if (attr.size() != 0) {
            for (Element element : attr) {
                String jumpUrl = element.attr("href").trim();
//                    log.info("找到调整爱电影-->" +jumpUrl);
                if (jumpUrl.contains(lxxhUrl)) {
                    movieUrlInLxxh.add(jumpUrl);
                }
            }
        }
        //直接获取百度网盘  这段代码可能有问题
        if (movieUrlInLxxh.size() == 0) {
            movieUrlInLxxh.add(urlAiDianying);

        }
        return movieUrlInLxxh;
    }


    public ArrayList<MovieNameAndUrlModel> getWangPanByLxxh(String secondUrlLxxh, String proxyIpAndPort) {

        ArrayList<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList();
        log.info("爱电影--》" + secondUrlLxxh);
        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
        movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);

        //采用phantomJs 无界浏览器形式访问
        PhantomJSDriver secorndBrowserDriver = phantomJsProxyCallService.create(secondUrlLxxh, proxyIpAndPort);
        System.out.println("----------");
        System.out.println(secorndBrowserDriver.getPageSource());
        Document secorndDocument = Jsoup.parse(secorndBrowserDriver.getPageSource());

        movieNameAndUrlModel.setMovieName(secorndDocument.getElementsByTag("title").first().text());

        Elements secorndAttr = secorndDocument.getElementsByTag("p").select("span");
        for (Element element : secorndAttr) {
            for (Element aTag : element.getElementsByTag("a")) {
                String linkhref = aTag.attr("href");
                if (linkhref.contains("pan.baidu.com")) {
                    log.info("这里已经拿到要爬取的url : " + linkhref);
                    movieNameAndUrlModel.setWangPanUrl(linkhref);
                    movieNameAndUrlModel.setWangPanPassword("密码：LXXH");
                    break;
                } else {
                    continue;
                }
            }
        }
//     第二种情况 span标签 里没有 url
        if (StrUtil.isBlank(movieNameAndUrlModel.getWangPanUrl())) {
            Elements urlFinals = secorndDocument.getElementsByTag("p").select("a");
            for (Element urlFianl : urlFinals) {
                String linkhref = urlFianl.attr("href");
                if (linkhref.contains("pan.baidu.com")) {
                    log.info("这里已经拿到要爬取的url : " + linkhref);
                    movieNameAndUrlModel.setWangPanUrl(linkhref);
                    movieNameAndUrlModel.setWangPanPassword("密码：LXXH");
                    System.out.println(linkhref);
                    break;
                } else {
                    continue;
                }
            }
        }
        movieNameAndUrlModelList.add(movieNameAndUrlModel);
        return movieNameAndUrlModelList;
    }


    @Async("crawler-Executor")
    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIpAndPort) {
        Set<String> movieUrlInLxxh = firstFindLxxhUrl(searchMovieName, proxyIpAndPort);
        ArrayList<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList();
        log.info("-------------------------开始爬取爱电影 begin ----------------------------");

        try {
            for (String secondUrlLxxh : movieUrlInLxxh) {
                movieNameAndUrlModelList.addAll(getWangPanByLxxh(secondUrlLxxh, proxyIpAndPort));
            }
            //由于包含模糊查询、这里记录到数据库中做插入更新操作
            movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, "url_movie_aidianying");
            invalidUrlCheckingService.checkUrlMethod("url_movie_aidianying", movieNameAndUrlModelList);
            redisTemplate.opsForHash().put("aidianying", searchMovieName, movieNameAndUrlModelList);
            redisTemplate.expire(searchMovieName, 60, TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("searchMovieName --> " + searchMovieName);
            log.error("AiDianyingService.saveOrFreshRealMovieUrl()  ->" + e.getMessage());
        }

    }


}