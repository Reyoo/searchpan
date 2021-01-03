package com.libbytian.pan.wechat.service;


import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: pan
 * @Package: com.search.pan.system.service
 * @ClassName: NormalPageService
 * @Author: sun71
 * @Description: 获取
 * @Date: 2020/8/30 16:19
 * @Version: 1.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class NormalPageService {
    private final RestTemplate restTemplate;


    @Value("${user.agent}")
    String userAgent;


    /**
     * 未读影单
     * @param url
     * @return
     */
    public List<MovieNameAndUrlModel> getNormalUrl(String url) {
        LocalTime begin = LocalTime.now();
        List<MovieNameAndUrlModel> movieList = new ArrayList<>();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("User-Agent", userAgent);
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
        ResponseEntity<String> resultResponseEntity = this.restTemplate.exchange(
                String.format(url),
                HttpMethod.GET, requestEntity, String.class);
        if (resultResponseEntity.getStatusCode() == HttpStatus.OK) {
            String html = resultResponseEntity.getBody();
            System.out.println("==================");
            System.out.println(html);
            System.out.println("==================");
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select("article");


            for (Element element : elements) {
                MovieNameAndUrlModel movieNameAndUrl = new MovieNameAndUrlModel();
                movieNameAndUrl.setMovieName(element.select("a").get(1).text());
                movieNameAndUrl.setMovieUrl(element.select("a").attr("href"));
                movieList.add(movieNameAndUrl);
            }


        }
        LocalTime end = LocalTime.now();
        Duration duration = Duration.between(begin, end);
        System.out.println("接口请求 ---- > Duration: " + duration);
        return movieList;
    }


    /**
     * 未读影单 根据搜索到的文本内容的sid 搜索sid后的资源
     * @param movieNameAndUrlModel
     * @return
     */
    public MovieNameAndUrlModel getMoviePanUrl(MovieNameAndUrlModel movieNameAndUrlModel) {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("User-Agent", userAgent);
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
        ResponseEntity<String> resultResponseEntity = this.restTemplate.exchange(
                String.format(movieNameAndUrlModel.getMovieUrl()),
                HttpMethod.GET, requestEntity, String.class);
        if (resultResponseEntity.getStatusCode() == HttpStatus.OK) {
            String html = resultResponseEntity.getBody();
            Document document = Jsoup.parse(html);
            String name = document.getElementsByTag("title").first().text();
            String[] arr = name.split(" – ");
            name = arr[0];
            Element element = document.select("div[class=entry-content]").get(0);
            String wangpan = element.select("p").select("strong").select("a").get(0).text();

            String lianjie = element.select("p").select("strong").select("a").attr("href");

            movieNameAndUrlModel.setWangPanUrl(lianjie);
            Elements pages = element.select("p");
            for (Element page : pages) {
                boolean contains1 = page.toString().contains("提取码");
                boolean contains2 = page.toString().contains("密码");

                if (contains1 || contains2) {
                    movieNameAndUrlModel.setWangPanPassword(page.text());
                }
            }
        }
        return movieNameAndUrlModel;
    }

    public MovieNameAndUrlModel getMoviePanUrl2(MovieNameAndUrlModel movieNameAndUrlModel) {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("User-Agent", userAgent);
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
        ResponseEntity<String> resultResponseEntity = this.restTemplate.exchange(
                String.format(movieNameAndUrlModel.getMovieUrl()),
                HttpMethod.GET, requestEntity, String.class);
        if (resultResponseEntity.getStatusCode() == HttpStatus.OK) {
            String html = resultResponseEntity.getBody();
            Document document = Jsoup.parse(html);
            String name = document.getElementsByTag("title").first().text();
            String[] arr = name.split(" – ");
            name = arr[0];
            Element element = document.select("div[class=entry-content]").get(0);

            Elements elements = element.select("p");
            String lianjie = null;
            String password = null;
            for (Element wangpanurl : elements) {
                boolean contains = wangpanurl.toString().contains("pan.baidu.com");
                if (contains) {
                    lianjie = wangpanurl.select("a").attr("href");
                    movieNameAndUrlModel.setWangPanUrl(lianjie);
                    break;
                }
            }


            for (Element el : elements) {
                boolean contains = el.toString().contains("密码");
                if (contains) {
                    String[] arr2 = el.text().split(" ");

                    movieNameAndUrlModel.setWangPanPassword(arr2[1]);
                    break;
                }
            }

        }
        return movieNameAndUrlModel;
    }






}
