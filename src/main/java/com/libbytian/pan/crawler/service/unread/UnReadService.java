package com.libbytian.pan.crawler.service.unread;

import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import com.libbytian.pan.system.service.impl.InvalidUrlCheckingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: QiSun
 * @date: 2021-01-04
 * @Description: 未读影单 爬取类
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UnReadService {

    private final IMovieNameAndUrlService iMovieNameAndUrlService;
    private final InvalidUrlCheckingService invalidUrlCheckingService;
    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;

    @Value("${user.agent}")
    String userAgent;


    @Value("${user.unread.weiduyingdan}")
    String unreadUrl;



    /**
     * 获取百度网盘
     * @param url
     * @return
     */
    public MovieNameAndUrlModel getUnReadMovieLoops(String url) {
        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
        try {

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.add("User-Agent", userAgent);
            HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
            ResponseEntity<String> resultResponseEntity = this.restTemplate.exchange(
                    String.format(url),
                    HttpMethod.GET, requestEntity, String.class);
            if (resultResponseEntity.getStatusCode() == HttpStatus.OK) {
                String html = resultResponseEntity.getBody();
                System.out.println("=========================================");
                System.out.println(html);
                System.out.println("=========================================");
                Document document = Jsoup.parse(html);
                String name = document.getElementsByTag("title").first().text();
                System.out.println("******");
                System.out.println(name);
                System.out.println("******");

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

                    if (page.toString().contains("提取码")) {
                        movieNameAndUrlModel.setWangPanPassword(page.text());
                        break;
                    }
                    if (page.toString().contains("密码")) {
                        movieNameAndUrlModel.setWangPanPassword(page.text());
                        break;
                    }

                }
            }
            return movieNameAndUrlModel;
        } catch (
                Exception e) {
            log.error(e.getMessage());
            return movieNameAndUrlModel;
        }
    }


    /**
     * 未读影单 根据电影名获取第一层 id
     *
     * @param movieName
     * @return
     */
    public Set<String> getNormalUnReadUrl(String movieName) {

        String url = unreadUrl + "/?s=" + movieName;
        LocalTime begin = LocalTime.now();
        Set<String> movieList = new HashSet<>();
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
//                MovieNameAndUrlModel movieNameAndUrl = new MovieNameAndUrlModel();
//                movieNameAndUrl.setMovieName(element.select("a").get(1).text());
                movieList.add(element.select("a").attr("href"));
//                movieList.add(movieNameAndUrl);
            }

        }
        LocalTime end = LocalTime.now();
        Duration duration = Duration.between(begin, end);
        System.out.println("接口请求 ---- > Duration: " + duration);
        return movieList;
    }


    /**
     * 根据电影名获取未读影单的百度盘资源
     *
     * @param searchMovieName
     * @return
     */
    public List<MovieNameAndUrlModel> getUnReadCrawlerResult(String searchMovieName) {

        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
        Set<String> set = getNormalUnReadUrl(searchMovieName);

        try {
            if (set.size() > 0) {
                for (String url : set) {
                    //由于包含模糊查询、这里记录到数据库中做插入更新操作
                    MovieNameAndUrlModel movieNameAndUrlModel = getUnReadMovieLoops(url);
                    movieNameAndUrlModelList.add(movieNameAndUrlModel);
                }
            }

            //判断URL 可用性  可用则插入更新 否则则删除

            List<MovieNameAndUrlModel> couldUseMovieUrl = invalidUrlCheckingService.checkUrlMethod("url_movie_unread", movieNameAndUrlModelList);
            redisTemplate.opsForHash().putIfAbsent("unreadmovie", searchMovieName, couldUseMovieUrl);

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return movieNameAndUrlModelList;

    }


}
