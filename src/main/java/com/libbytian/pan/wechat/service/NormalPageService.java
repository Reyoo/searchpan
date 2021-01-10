package com.libbytian.pan.wechat.service;


import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.util.UserAgentUtil;
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


    public MovieNameAndUrlModel getMovieLoops(String url) {
        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
        try {

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("User-Agent", UserAgentUtil.randomUserAgent());
            HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
            ResponseEntity<String> resultResponseEntity = this.restTemplate.exchange(
                    String.format(url),
                    HttpMethod.GET, requestEntity, String.class);
            if (resultResponseEntity.getStatusCode() == HttpStatus.OK) {
                String html = resultResponseEntity.getBody();
//                System.out.println("=========================================");
//                System.out.println(html);
//                System.out.println("=========================================");
                Document document = Jsoup.parse(html);
                String name = document.getElementsByTag("title").first().text();
//                System.out.println("******");
//                System.out.println(name);
//                System.out.println("******");

                String[] arr = name.split(" – ");
                name = arr[0];
                Element element = document.select("div[class=entry-content]").get(0);
                ;
//                String wangpan = element.select("p").select("strong").select("a").get(0).text();

                String lianjie = element.select("p").select("strong").select("a").attr("href");


                Elements pages = element.select("p");


                movieNameAndUrlModel.setMovieUrl(url);
                movieNameAndUrlModel.setMovieName(name);
                movieNameAndUrlModel.setWangPanUrl(lianjie);
                for (Element page : pages) {
                    page.getElementsByTag("提取码");
                    boolean contains1 = page.toString().contains("提取码");
                    boolean contains2 = page.toString().contains("密码");

                    if (contains1 || contains2) {
                        movieNameAndUrlModel.setWangPanPassword(page.text());
                    }
                }
            }
            return movieNameAndUrlModel;
        } catch (Exception e) {
            log.error(e.getMessage());
            return movieNameAndUrlModel;
        }
    }









    public MovieNameAndUrlModel getMoviePanUrl2(MovieNameAndUrlModel movieNameAndUrlModel) {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("User-Agent", UserAgentUtil.randomUserAgent());
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


    /**
     * 莉莉
     * @param url
     * @return
     */
    public MovieNameAndUrlModel getMovieLoopsLiLi(String url) {

        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();

        movieNameAndUrlModel.setMovieUrl(url);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("User-Agent", UserAgentUtil.randomUserAgent());
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);

        ResponseEntity<String> resultResponseEntity = this.restTemplate.exchange(
                String.format(url),
                HttpMethod.GET, requestEntity, String.class);

        if (resultResponseEntity.getStatusCode() == HttpStatus.OK) {
            String html = resultResponseEntity.getBody();
//                System.out.println("=========================================");
//                System.out.println(html);
//                System.out.println("=========================================");
            Document document = Jsoup.parse(html);
            String name = document.getElementsByTag("title").first().text();
            movieNameAndUrlModel.setMovieName(name);
//            System.out.println("******");
//            System.out.println(name);
//            System.out.println("******");

            Elements attr = document.getElementsByTag("p");
            for (Element element : attr) {
                for (Element aTag : element.getElementsByTag("a")) {

                    String linkhref = aTag.attr("href");
                    if (linkhref.startsWith("pan.baidu.com")) {
                        log.info("这里已经拿到要爬取的url : " + linkhref);
                        movieNameAndUrlModel.setWangPanUrl(linkhref);
//                        System.out.println(linkhref);
                    }

                }
                if (element.text().contains("密码")) {
                    movieNameAndUrlModel.setWangPanPassword(element.text().split("【")[0].split(" ")[1]);
                }
            }
//            System.out.println("-----------------");
        }
        return movieNameAndUrlModel;


    }



}
