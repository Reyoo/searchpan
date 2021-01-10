package com.libbytian.pan.crawler.service.aidianying;

import cn.hutool.core.util.StrUtil;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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


    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;
    private final InvalidUrlCheckingService invalidUrlCheckingService;


    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;

    public Set<String> getNormalUrlAidianying(String searchMovieName) {
        Set aiDianYingNormalUrlSet = new HashSet();
        String url = lxxhUrl + "/?s=" + searchMovieName;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("User-Agent", UserAgentUtil.randomUserAgent());
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
        ResponseEntity<String> resultResponseEntity = this.restTemplate.exchange(
                String.format(url),
                HttpMethod.GET, requestEntity, String.class);
        if (resultResponseEntity.getStatusCode() == HttpStatus.OK) {
            String html = resultResponseEntity.getBody();
/*            System.out.println("=========================================");
            System.out.println(html);
            System.out.println("=========================================");*/
            Document document = Jsoup.parse(html);


            Elements attr = document.getElementsByClass("entry-thumb entry-cover");
            ;
            for (Element element : attr) {
                aiDianYingNormalUrlSet.add(element.attr("href").trim());
            }
        }

        return aiDianYingNormalUrlSet;
    }


    /**
     * 爱电影  第二次传ID调用
     * http://www.lxxh7.com/随机/随机/93687LjLXH.html#comments
     *
     * @param url
     * @return
     */
    public MovieNameAndUrlModel getMovieLoopsAiDianying(String url) {
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
/*                System.out.println("=========================================");
                System.out.println(html);
                System.out.println("=========================================");*/
                Document document = Jsoup.parse(html);
                String name = document.getElementsByTag("title").first().text();
                movieNameAndUrlModel.setMovieName(name);
//                System.out.println("******");
//                System.out.println(name);
//                System.out.println("******");



                /* 完整爬取 比较消耗资源
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
                }*/


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
    }

    /**
     * 根据电影名获取爱电影的百度盘资源
     *
     * @param searchMovieName
     * @return
     */
    public List<MovieNameAndUrlModel> getAiDianYingCrawlerResult(String searchMovieName) {

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
            List<MovieNameAndUrlModel> couldUseMovieUrl = new ArrayList<>();
            //存入数据库 做可链接校验
            couldUseMovieUrl = invalidUrlCheckingService.checkUrlMethod("url_movie_aidianying", couldUseMovieUrl);
            redisTemplate.opsForHash().putIfAbsent("aidianying", searchMovieName, couldUseMovieUrl);

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return movieNameAndUrlModelList;

    }


}
