package com.libbytian.pan.crawler.service.xiaoyou;

import com.libbytian.pan.proxy.service.GetProxyService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import com.libbytian.pan.system.service.impl.InvalidUrlCheckingService;
import com.libbytian.pan.system.util.UserAgentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 项目名: pan
 * 文件名: XiaoYouService
 * 创建者: HS
 * 创建时间:2021/1/18 16:02
 * 描述: TODO
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class XiaoYouService {


    private final RedisTemplate redisTemplate;
    private final RestTemplate restTemplate;
    private final InvalidUrlCheckingService invalidUrlCheckingService;
    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final GetProxyService getProxyService;



    @Value("$(user.xiaoyou.yingmiao)")
    String xiaoyouUrl;

    public void  getXiaoYouCrawlerResult(String searchMovieName,String proxyIp, int proxyPort) {
        log.info("-------------->开始爬取 影喵儿<--------------------");
        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
        try {
            Set<String> set = getNormalXiaoYouUrl(searchMovieName,proxyIp,proxyPort);
            if (set.size() > 0) {
                for (String url : set) {
                    //由于包含模糊查询、这里记录到数据库中做插入更新操作
                    movieNameAndUrlModelList.addAll(getXiaoYouMovieLoops(url,proxyIp,proxyPort));
                }

                invalidUrlCheckingService.checkUrlMethod("url_movie_xiaoyou",movieNameAndUrlModelList);
            }

            //存入数据库
            movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList, "url_movie_xiaoyou");
            //存入redis
            redisTemplate.opsForHash().put("xiaoyoumovie", searchMovieName, movieNameAndUrlModelList);


        } catch (Exception e) {
            getProxyService.removeUnableProxy(proxyIp + ":" + proxyPort);
            log.error(e.getMessage());
        }

    }


    /**
     * 获取 爬虫 第一层url集合
     * @param searchMovieName
     * @return
     * @throws IOException
     */
    public Set<String> getNormalXiaoYouUrl(String searchMovieName , String proxyIp , int proxyPort) throws IOException {

        log.info("-------------->开始爬取 小悠<--------------------");

        Set<String> movieList = new HashSet<>();
        String url = "http://y.yuanxiao.net.cn" + "/?s=" + searchMovieName;

        try {
            Document document = Jsoup.connect(url)
                    .proxy(proxyIp, proxyPort).
                    userAgent(UserAgentUtil.randomUserAgent())
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .header("Cache-Control", "max-age=0")
                    .header("Accept-Encoding", "gzip, deflate, sdch")
                    .header("Accept-Language", "zh-CN,zh;q=0.8")
                    .header("Upgrade-Insecure-Requests", "1")
                    .timeout(12000)
                    .followRedirects(false).get();


            //拿到查询结果 片名及链接
            Elements elements = document.getElementById("container").getElementsByClass("entry-title");


            for (Element element : elements) {
                String movieUrl = element.select("a").attr("href");

                //判断是一层链接还是两层链接
                if (StringUtils.isBlank(movieUrl)){
                    movieList.add(url);
                }else {
                    movieList.add(movieUrl);
                }
            }
            return movieList;
        } catch (IOException e) {
            log.error(e.getMessage());
            getProxyService.removeUnableProxy(proxyIp + ":" + proxyPort);
            return movieList;
        }
    }



    /**
     * 获取 爬虫 第二层url集合
     * @param url
     * @return
     * @throws IOException
     */
    public  List<MovieNameAndUrlModel> getXiaoYouMovieLoops(String url ,String proxyIp ,int proxyPort) throws IOException {

        List<MovieNameAndUrlModel> list = new ArrayList();

        try {

            HttpHeaders requestHeaders = new HttpHeaders();
            String userAgent = UserAgentUtil.randomUserAgent();

            requestHeaders.add("User-Agent", userAgent);
            HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);

            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setProxy(
                    //设置代理服务
                    new Proxy(
                            Proxy.Type.HTTP,
                            new InetSocketAddress(proxyIp, proxyPort)
                    )
            );
            this.restTemplate.setRequestFactory(requestFactory);


            ResponseEntity<String> resultResponseEntity = this.restTemplate.exchange(
                    String.format(url),
                    HttpMethod.GET, requestEntity, String.class);

            String html = resultResponseEntity.getBody();

            Document document = Jsoup.parse(html);

//            Document document = Jsoup.connect(url)
//                    .proxy(proxyIp, proxyPort).
//                            get();

//            Document document = Jsoup.connect(url).get();

            String movieName = document.getElementsByTag("title").first().text();

            String[] arrName = movieName.split("- 小悠家");
            movieName = arrName[0];

            Elements pTagAttr = document.getElementsByTag("p");

            for (Element element : pTagAttr) {
                if (element.select("a").attr("href").contains("pan.baidu")) {
                    MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();

                    if (element.childNodeSize() == 3){

    //                    String password = element.childNode(2).toString().split("&nbsp; &nbsp; ")[1];
                        String password = element.childNode(2).toString().replaceAll("&nbsp;","");

                        movieNameAndUrlModel.setWangPanPassword(password);
                    }

                    //判断片名是否需要拼接
                    int indexName = element.childNode(0).toString().indexOf(".视频：");
                    if (indexName == -1){
                        movieNameAndUrlModel.setMovieName(movieName);
                    }else {
                        movieNameAndUrlModel.setMovieName(movieName + element.childNode(0).toString().substring(0,indexName));
                    }

                    movieNameAndUrlModel.setWangPanUrl(element.select("a").attr("href"));
                    movieNameAndUrlModel.setMovieUrl(url);
                    list.add(movieNameAndUrlModel);
                } else {
                    continue;
                }

            }

            return list;
        } catch (RestClientException e) {
            e.printStackTrace();
            return list;
        }
    }






}
