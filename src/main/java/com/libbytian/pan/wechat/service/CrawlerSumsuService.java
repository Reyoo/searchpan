package com.libbytian.pan.wechat.service;

import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import jdk.nashorn.internal.runtime.ECMAException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.IOP.Encoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class CrawlerSumsuService {
    private final RestTemplate restTemplate;


    private final IMovieNameAndUrlService movieNameAndUrlService;

    @Value("${user.agent}")
    String userAgent;

    @Value("${user.sumsu.url}")
    String url;

    /**
     * 获取 社区动力内容
     *
     * @param movieName
     * @return
     */
    public List<MovieNameAndUrlModel> getSumsuUrl(String movieName) throws Exception {
        List<String> firstSearchUrls = new ArrayList<>();
        LocalTime begin = LocalTime.now();
        List<MovieNameAndUrlModel> movieList = new ArrayList<>();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("User-Agent", userAgent);
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("formhash", "a07b2e14");


        map.add("srchtxt", movieName);
        map.add("searchsubmit", "yes");

        //重定向
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        HttpClient httpClient = HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();
        factory.setHttpClient(httpClient);
        this.restTemplate.setRequestFactory(factory);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, requestHeaders);
        ResponseEntity<String> resultResponseEntity = this.restTemplate.exchange(
                String.format(url),
                HttpMethod.POST, requestEntity, String.class);


        if (resultResponseEntity.getStatusCode() == HttpStatus.OK) {
            String html = resultResponseEntity.getBody();
            System.out.println("==================");
            System.out.println(html);
            System.out.println("==================");
            Document doc = Jsoup.parse(html);

            Elements elements = doc.select("li").select("a");

            //获取到第一层的中文搜索  继而拿到tid  查询详细电影

            for (Element link : elements) {
                String linkhref = link.attr("href");
                if (linkhref.startsWith("forum.php?mod")) {
                    firstSearchUrls.add("http://520.sumsu.cn/" + linkhref);
                    log.info("查询电影名为--> " + movieName + " 获取第一次链接为--> " + linkhref);
                }

            }

            log.info("查询电影名为---> " + movieName + "  第一层次查询完,进入第二次查询获取网盘url");


            if (firstSearchUrls.size() > 0) {
                movieList =  getTidSumsuUrl(firstSearchUrls);

            }

        }
        LocalTime end = LocalTime.now();
        Duration duration = Duration.between(begin, end);
        System.out.println("接口请求 ---- > Duration: " + duration);
        return movieList;
    }


    public List<MovieNameAndUrlModel> getTidSumsuUrl(List<String> urls) {

        List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();
        try {


            for (String sumsuUrl : urls) {
                System.out.println(sumsuUrl);
                HttpHeaders requestSumsuHeaders = new HttpHeaders();
                requestSumsuHeaders.add("User-Agent", userAgent);
                HttpEntity<String> requestSumsuEntity = new HttpEntity(null, requestSumsuHeaders);
                ResponseEntity<String> resultSumsuResponseEntity = this.restTemplate.exchange(
                        sumsuUrl,
                        HttpMethod.GET, requestSumsuEntity, String.class);

                if (resultSumsuResponseEntity.getStatusCode() == HttpStatus.OK) {
                    String tidHtml = resultSumsuResponseEntity.getBody();
                    System.out.println("*****");
                    System.out.println(tidHtml);
                    System.out.println("*****");
                    Document tidDoc = Jsoup.parse(tidHtml);

                    if( tidDoc.title().contains("404")){
                        continue;
                    }

                    String movieName = tidDoc.title();
                    Elements elements = tidDoc.select("strong").select("a");


                    for (Element link : elements) {
                        String linkhref = link.attr("href");
                        if (linkhref.startsWith("https://pan.baidu.com")) {
                            System.out.println("--------------------------------");
                            MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                            String baiPan = link.attr("href").toString();
                            movieNameAndUrlModel.setWangPanUrl(baiPan);
                            movieNameAndUrlModel.setMovieName(tidDoc.title());
                            movieNameAndUrlModel.setMovieUrl(sumsuUrl);

                            if(link.parent().text().contains("提取码:")){
                                movieNameAndUrlModel.setWangPanPassword(link.parent().text().split("提取码:")[1].trim());
                            }

                            if(link.parent().text().contains("提取码：")){
                                movieNameAndUrlModel.setWangPanPassword(link.parent().text().split("提取码：")[1].trim());
                            }

                            if(link.parent().text().contains("密码:")){
                                movieNameAndUrlModel.setWangPanPassword(link.parent().text().split("密码:")[1].trim());
                            }

                            if(link.parent().text().contains("密码：")){
                                movieNameAndUrlModel.setWangPanPassword(link.parent().text().split("密码：")[1].trim());
                            }

                            movieNameAndUrlModels.add(movieNameAndUrlModel);
                        }

                    }
                    movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModels, "url_movie_sumsu");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return movieNameAndUrlModels;
    }


    /**
     * 数据库初始化 time 循环次数
     * @param times
     * @return
     */
    public List<MovieNameAndUrlModel> firstInitTidSumsuUrl(int times) {

        List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();
        try {
                String url = "http://520.sumsu.cn/forum.php?mod=viewthread&tid=" + times + "&highlight=%BD%AB%BE%FC&mobile=2";
                System.out.println(url);
                HttpHeaders requestSumsuHeaders = new HttpHeaders();
                requestSumsuHeaders.add("User-Agent", userAgent);
                HttpEntity<String> requestSumsuEntity = new HttpEntity(null, requestSumsuHeaders);
                ResponseEntity<String> resultSumsuResponseEntity = this.restTemplate.exchange(
                        url,
                        HttpMethod.GET, requestSumsuEntity, String.class);

                if (resultSumsuResponseEntity.getStatusCode() == HttpStatus.OK) {
                    String tidHtml = resultSumsuResponseEntity.getBody();

                    System.out.println(tidHtml);
                    System.out.println("************");
                    Document tidDoc = Jsoup.parse(tidHtml);
                    if( tidDoc.title().contains("404")){
                        return movieNameAndUrlModels;
                    }
                    String movieName = tidDoc.title();

                    if(movieName.contains("百度云下载链接")){
                        movieName = tidDoc.title().split("百度云下载链接")[0].trim();
                    }

                    else if(movieName.contains("Powered by Discuz")){
                        movieName = movieName.split("Powered by Discuz")[0].trim();
                    }

                    Elements elements = tidDoc.select("strong").select("a");


                    for (Element link : elements) {
                        String linkhref = link.attr("href");
                        if (linkhref.startsWith("https://pan.baidu.com")) {
                            MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
                            String baiPan = link.attr("href").toString();
                            movieNameAndUrlModel.setWangPanUrl(baiPan);
                            movieNameAndUrlModel.setMovieName(movieName);
                            movieNameAndUrlModel.setMovieUrl(url);
                            if(link.parent().text().contains("提取码:")){
                                movieNameAndUrlModel.setWangPanPassword(link.parent().text().split("提取码:")[1].trim());
                            }

                            if(link.parent().text().contains("提取码：")){
                                movieNameAndUrlModel.setWangPanPassword(link.parent().text().split("提取码：")[1].trim());
                            }

                            if(link.parent().text().contains("密码:")){
                                movieNameAndUrlModel.setWangPanPassword(link.parent().text().split("密码:")[1].trim());
                            }

                            if(link.parent().text().contains("密码：")){
                                movieNameAndUrlModel.setWangPanPassword(link.parent().text().split("密码：")[1].trim());
                            }



                            movieNameAndUrlModels.add(movieNameAndUrlModel);
                        }

                    }
                    movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModels, "url_movie_sumsu");
                }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return movieNameAndUrlModels;
    }

}
