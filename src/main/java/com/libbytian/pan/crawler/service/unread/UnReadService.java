package com.libbytian.pan.crawler.service.unread;

import cn.hutool.core.util.StrUtil;
import com.libbytian.pan.crawler.service.aidianying.AiDianyingService;
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

    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final InvalidUrlCheckingService invalidUrlCheckingService;
    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;


    @Value("${user.unread.weiduyingdan}")
    String unreadUrl;


    /**
     * 获取百度网盘
     *
     * @param url
     * @return
     */
    public MovieNameAndUrlModel getUnReadMovieLoops(String url) {
        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
        String linkhref = null;
        try {
            movieNameAndUrlModel.setMovieUrl(url);
            HttpHeaders requestHeaders = new HttpHeaders();
            String userAgent = UserAgentUtil.randomUserAgent();
//            System.out.println("***********************");
//            System.out.println(userAgent);
//            System.out.println("***********************");
            requestHeaders.add("User-Agent", userAgent);
            HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
            ResponseEntity<String> resultResponseEntity = this.restTemplate.exchange(
                    String.format(url),
                    HttpMethod.GET, requestEntity, String.class);
            String wangPanUrl = null;
            if (resultResponseEntity.getStatusCode() == HttpStatus.OK) {
                String html = resultResponseEntity.getBody();
//                System.out.println("=========================================");
//                System.out.println(html);
//                System.out.println("=========================================");
                Document document = Jsoup.parse(html);
                String name = document.getElementsByTag("title").first().text();

                if (name.contains("– 未读影单")) {
                    name = name.split("– 未读影单")[0].trim();
                }

                movieNameAndUrlModel.setMovieName(name);


                Elements attr = document.getElementsByTag("p");
                for (Element passwdelement : attr) {

                    if (passwdelement.text().contains("密码:")) {
                        movieNameAndUrlModel.setWangPanPassword(passwdelement.text());
//                        System.out.println(passwdelement.text());
                        break;
                    }

                    if (passwdelement.text().contains("密码：")) {
                        movieNameAndUrlModel.setWangPanPassword(passwdelement.text());
//                        System.out.println(passwdelement.text());
                        break;
                    }

                    if (passwdelement.text().contains("提取码:")) {
                        movieNameAndUrlModel.setWangPanPassword(passwdelement.text());
//                        System.out.println(passwdelement.text());
                        break;
                    }

                    if (passwdelement.text().contains("提取码：")) {
                        movieNameAndUrlModel.setWangPanPassword(passwdelement.text());
//                        System.out.println(passwdelement.text());
                        break;
                    }
                }


                //第一种情况; h2 标签内进行
                Elements tagH2attr = document.getElementsByTag("h2").select("a");
                for (Element hrAttrsinnera : tagH2attr) {
                    wangPanUrl = hrAttrsinnera.attr("href");
                    if (wangPanUrl.contains("pan.baidu.com")) {
                        break;
                    } else {
                        continue;
                    }
                }
                //第二种情况 ; p标签下的 a标签
                if (StrUtil.isBlank(wangPanUrl) || !wangPanUrl.contains("pan.baidu")) {
                    Elements tagPattr = document.getElementsByTag("p").select("a");
                    for (Element hrAttrsinnera : tagPattr) {
                        wangPanUrl = hrAttrsinnera.attr("href");
                        if (wangPanUrl.contains("pan.baidu.com")) {
                            break;
                        } else {
                            continue;
                        }
                    }
                }

                //第三种情况 ; h3标签下的 a标签
                if (StrUtil.isBlank(wangPanUrl) || !wangPanUrl.contains("pan.baidu")) {
                    Elements tagH3attr = document.getElementsByTag("h3").select("a");
                    for (Element h3Attrsinnera : tagH3attr) {
                        wangPanUrl = h3Attrsinnera.attr("href");
                        if (wangPanUrl.contains("pan.baidu.com")) {
                            break;
                        } else {
                            continue;
                        }
                    }
                }

                //第4种情况 ; div标签下的 a标签
                if (StrUtil.isBlank(wangPanUrl) || !wangPanUrl.contains("pan.baidu")) {
                    Elements tagDivattr = document.getElementsByTag("div").select("a");
                    for (Element divAttrsinnera : tagDivattr) {
                        wangPanUrl = divAttrsinnera.attr("href");
                        if (wangPanUrl.contains("pan.baidu.com")) {
                            break;
                        } else {
                            continue;
                        }
                    }
                }

            }
            movieNameAndUrlModel.setWangPanUrl(wangPanUrl);
            return movieNameAndUrlModel;
        } catch (Exception e) {
            log.error(e.getMessage());
            e.getMessage();
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
        AiDianyingService.getHttpHeader(url, this.restTemplate);
        ResponseEntity<String> resultResponseEntity = AiDianyingService.getHttpHeader(url, this.restTemplate);

        if (resultResponseEntity.getStatusCode() == HttpStatus.OK) {
            String html = resultResponseEntity.getBody();
//            System.out.println("==================");
//            System.out.println(html);
//            System.out.println("==================");
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

            //判断URL 可用性  可用则插入更新 否则则删除  摘掉百度校验！！！！！！！！！！！！！！！！！！！！
//
//            List<MovieNameAndUrlModel> couldUseMovieUrl = invalidUrlCheckingService.checkUrlMethod("url_movie_unread", movieNameAndUrlModelList);
//            redisTemplate.opsForHash().putIfAbsent("unreadmovie", searchMovieName, couldUseMovieUrl);
            /**
             * 摘掉 百度校验 版本
             */
            movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList,"url_movie_unread");
            redisTemplate.opsForHash().putIfAbsent("unreadmovie", searchMovieName, movieNameAndUrlModelList);


        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return movieNameAndUrlModelList;

    }


}
