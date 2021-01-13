package com.libbytian.pan.crawler.service.aidianying;

import cn.hutool.core.util.StrUtil;
import com.libbytian.pan.proxy.service.GetProxyService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import com.libbytian.pan.system.service.impl.InvalidUrlCheckingService;
import com.libbytian.pan.system.util.UserAgentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private final GetProxyService getProxyService;


    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;


    public void saveOrFreshRealMovieUrl(String searchMovieName, String proxyIp, int proxyPort) {
        ArrayList<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList();
        try {

            Set<String> movieUrlInLxxh = getNormalUrlAidianying(searchMovieName, proxyIp, proxyPort);
            //说明搜索到了 url 电影路径
            if (movieUrlInLxxh.size() > 0) {
                for (String url : movieUrlInLxxh) {
                    //由于包含模糊查询、这里记录到数据库中做插入更新操作
                    MovieNameAndUrlModel movieNameAndUrlModel = getMovieLoopsAiDianying(url, proxyIp, proxyPort);
                    movieNameAndUrlModelList.add(movieNameAndUrlModel);
                }
            }
            movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, "url_movie_aidianying");
            redisTemplate.opsForHash().putIfAbsent("aidianying", searchMovieName, movieNameAndUrlModelList);
            redisTemplate.expire(searchMovieName, 60, TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error("searchMovieName --> " + searchMovieName);
            log.error("AiDianyingService.saveOrFreshRealMovieUrl()  ->" + e.getMessage());
        }

    }


    /**
     * 新版本获取关键词搜索爬虫第一步
     *
     * @param searchMovieName
     * @return
     */
    public Set<String> getNormalUrlAidianying(String searchMovieName, String proxyIp, int proxyPort) {
        Set<String> aiDianYingNormalUrlSet = new HashSet();

        try {
//            String movieEncodeStr = URLEncoder.encode(searchMovieName, "UTF8");
            String url = lxxhUrl + "/?s=" + searchMovieName;

            Connection.Response response = Jsoup.connect(url)
                    .proxy(proxyIp, proxyPort)
                    .userAgent(UserAgentUtil.randomUserAgent())
                    .timeout(8000)
                    .referrer("http://www.lxxh7.com")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .header("Cache-Control", "max-age=0")
                    .header("Accept-Encoding", "gzip, deflate, sdch")
                    .header("Accept-Language", "zh-CN,zh;q=0.8")
                    .header("Upgrade-Insecure-Requests", "1")
                    .followRedirects(true).execute();
            System.out.println(response.url());

            if (!response.url().toString().contains("/?s=")) {
                aiDianYingNormalUrlSet.add(response.url().toString());
            } else {
                Document document = Jsoup.connect(response.url().toString()).proxy(proxyIp, proxyPort).userAgent(UserAgentUtil.randomUserAgent()).timeout(10000).referrer("http://www.lxxh7.com").followRedirects(false).get();
                System.out.println(document.text());
                System.out.println(document.body());
                System.out.println(document.outerHtml());
                Elements attr = document.getElementsByTag("h2").select("a");
                for (Element element : attr) {
                    System.out.println(element.attr("href").trim());
                    aiDianYingNormalUrlSet.add(element.attr("href").trim());
                }
            }
        } catch (Exception e) {
            getProxyService.removeUnableProxy(proxyIp + ":" + proxyPort);
            log.error(e.getMessage());
        }
        return aiDianYingNormalUrlSet;
    }


    /**
     * 最新版本   二次获取 爬取的电影   http://www.zhimaruanjian.com/
     *
     * @param secondUrlLxxh
     * @return
     */
    public MovieNameAndUrlModel getMovieLoopsAiDianying(String secondUrlLxxh, String proxyIp, int proxyPort) {
        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();


        try {
            movieNameAndUrlModel.setMovieUrl(secondUrlLxxh);

            Document document = Jsoup.connect(secondUrlLxxh)
                    .proxy(proxyIp, proxyPort)
                    .userAgent(UserAgentUtil.randomUserAgent())
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .header("Cache-Control", "max-age=0")
                    .header("Upgrade-Insecure-Requests", "1")
                    .timeout(10000)
                    .referrer("http://www.lxxh7.com")
                    .get();
            String name = document.getElementsByTag("title").first().text();
            movieNameAndUrlModel.setMovieName(name);

            Elements attr = document.getElementsByTag("p").select("span");
            for (Element element : attr) {
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
//                第二种情况 span标签 里没有 url
            if (StrUtil.isBlank(movieNameAndUrlModel.getWangPanUrl())) {
                Elements urlFinals = document.getElementsByTag("p").select("a");
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

        } catch (Exception e) {
            getProxyService.removeUnableProxy(proxyIp + ":" + proxyPort);
            log.error(e.getMessage());
            e.getMessage();
        }
        return movieNameAndUrlModel;
    }


    public static ResponseEntity getHttpHeader(String url, RestTemplate restTemplate, String proxyIp, int proxyPort) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setProxy(
                //设置代理服务
                new Proxy(
                        Proxy.Type.HTTP,
                        new InetSocketAddress(proxyIp, proxyPort)
                )
        );
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("User-Agent", UserAgentUtil.randomUserAgent());
        requestHeaders.add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        requestHeaders.add("Host", "www.lxxh7.com");
        requestHeaders.add("Upgrade-Insecure-Requests", "1");
        requestHeaders.add("Cache-Control", "max-age=0");
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
        ResponseEntity<String> resultResponseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET, requestEntity, String.class);
        return resultResponseEntity;
    }
    /**
     * 爱电影  第二次传ID调用  老版本
     * http://www.lxxh7.com/随机/随机/93687LjLXH.html#comments
     *
     * @param url
     * @return
     */
   /* public MovieNameAndUrlModel getMovieLoopsAiDianying(String url) {
        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
        try {
            movieNameAndUrlModel.setMovieUrl(url);
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("User-Agent", UserAgentUtil.randomUserAgent());
            HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
            ResponseEntity<String> resultResponseEntity = this.restTemplate.exchange(
                    String.format(url),
                    HttpMethod.GET, requestEntity, String.class);
            if (resultResponseEntity.getStatusCode() == HttpStatus.OK) {
                String html = resultResponseEntity.getBody();
*//*                System.out.println("=========================================");
                System.out.println(html);
                System.out.println("=========================================");*//*
                Document document = Jsoup.parse(html);
                String name = document.getElementsByTag("title").first().text();
                movieNameAndUrlModel.setMovieName(name);
//                System.out.println("******");
//                System.out.println(name);
//                System.out.println("******");



                *//* 完整爬取 比较消耗资源
                Elements attr = document.getElementsByTag("p");
                for (Element element : attr) {
                    for (Element aTag : element.getElementsByTag("span").select("a")) {
                        System.out.println(aTag.text());
                        String linkhref = aTag.attr("href");
                        if (linkhref.contains("pan.baidu.com")) {
                            log.info("这里已经拿到要爬取的url : " + linkhref);
                            movieNameAndUrlModel.setWangPanUrl(linkhref);
                            System.out.println(linkhref);


                        } else {
                            continue;
                        }

                    }
                    if (element.text().contains("密码")) {
                        movieNameAndUrlModel.setWangPanPassword(element.text().split("【")[0].split(" ")[1]);
                        break;
                    }
                }*//*


//                第一种情况
                Elements attr = document.getElementsByTag("p").select("span");
                for (Element element : attr) {
                    for (Element aTag : element.getElementsByTag("a")) {
                        String linkhref = aTag.attr("href");
                        if (linkhref.contains("pan.baidu.com")) {
                            log.info("这里已经拿到要爬取的url : " + linkhref);
                            movieNameAndUrlModel.setWangPanUrl(linkhref);
                            movieNameAndUrlModel.setWangPanPassword("密码：LXXH");
//                            System.out.println(linkhref);
                            break;
                        } else {
                            continue;
                        }
                    }

                }
//                第二种情况 span标签 里没有 url
                if(StrUtil.isBlank(movieNameAndUrlModel.getWangPanUrl())){
                    Elements urlFinals = document.getElementsByTag("p").select("a");
                    for(Element urlFianl: urlFinals){
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

//                System.out.println("-----------------");
            }
            return movieNameAndUrlModel;
        } catch (Exception e) {
            return movieNameAndUrlModel;
        }
    }*/

    /**
     * 根据电影名获取爱电影的百度盘资源
     *
     * @param searchMovieName
     * @return
     */
  /*  public List<MovieNameAndUrlModel> getAiDianYingCrawlerResult(String searchMovieName) {

        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
        Set<String> set = getNormalUrlAidianying(searchMovieName);

        try {
            if (set.size() > 0) {
                for (String url : set) {
                    //由于包含模糊查询、这里记录到数据库中做插入更新操作
                    MovieNameAndUrlModel movieNameAndUrlModel = getMovieLoopsAiDianying(url);
                    movieNameAndUrlModelList.add(movieNameAndUrlModel);
                }
            }
//            List<MovieNameAndUrlModel> couldUseMovieUrl = new ArrayList<>();
//            //存入数据库 做可链接校验
//            couldUseMovieUrl = invalidUrlCheckingService.checkUrlMethod("url_movie_aidianying", couldUseMovieUrl);
//            redisTemplate.opsForHash().putIfAbsent("aidianying", searchMovieName, couldUseMovieUrl);



            movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList,"url_movie_aidianying");
            redisTemplate.opsForHash().putIfAbsent("aidianying", searchMovieName, movieNameAndUrlModelList);



        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return movieNameAndUrlModelList;

    }*/


//    }
}