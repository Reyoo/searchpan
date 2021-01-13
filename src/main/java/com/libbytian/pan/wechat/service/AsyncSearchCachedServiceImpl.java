package com.libbytian.pan.wechat.service;

import com.libbytian.pan.system.mapper.MovieNameAndUrlMapper;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import com.libbytian.pan.system.service.impl.InvalidUrlCheckingService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.crawler.service.aidianying.AiDianyingService;
import com.libbytian.pan.crawler.service.sumsu.CrawlerSumsuService;
import com.libbytian.pan.crawler.service.unread.UnReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.wechat.service
 * @ClassName: AsyncSearchCachedServiceImpl
 * @Author: sun71
 * @Description: 搜索电影名进Redis
 * @Date: 2020/10/14 16:34
 * @Version: 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@EnableAsync
public class AsyncSearchCachedServiceImpl {


    private final RedisTemplate redisTemplate;

    private final InvalidUrlCheckingService invalidUrlCheckingService;

    private final IMovieNameAndUrlService movieNameAndUrlService;


    private final CrawlerSumsuService crawlerSumsuService;

    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    private final AiDianyingService aiDianyingService;

    private final UnReadService unReadService;


    @Value("${user.unread.weiduyingdan}")
    String unreadUrl;

    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;


    /**
     * 根据不同表示返回不用结果
     *
     * @param searchMovieText
     * @param search
     * @return
     * @throws Exception
     */
    public List<MovieNameAndUrlModel> searchWord(String searchMovieText, String search,String proxyIp,int proxyPort) throws Exception {

        List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();
        switch (search) {
            case "a":
                //从爱电影获取资源返回aidianying

//                先从redis中获取
                movieNameAndUrlModels = (List<MovieNameAndUrlModel>) redisTemplate.opsForHash().get("aidianying", searchMovieText);

                if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
//数据库中没有从 mysql 中获取
                    movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_aidianying", searchMovieText);

//                    如果数据库中也没有 则从新爬取一遍
                    if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
                        crawlerAndSaveUrl(searchMovieText, "aidianying",proxyIp,proxyPort);
                    }

                    redisTemplate.opsForHash().putIfAbsent("aidianying", searchMovieText, movieNameAndUrlModels);
                    redisTemplate.expire(searchMovieText, 60, TimeUnit.SECONDS);
                    return movieNameAndUrlModels;
                } else {
                    return movieNameAndUrlModels;
                }

            case "u":
//                  从未读影单获取资源unreadmovie

                movieNameAndUrlModels = (List<MovieNameAndUrlModel>) redisTemplate.opsForHash().get("unreadmovie", searchMovieText);

                if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
                    //从数据库里拿
                    movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_unread", searchMovieText);
//                    校验暂时不做了 速度慢
//                    invalidUrlCheckingService.checkUrlMethod("url_movie_unread", movieNameAndUrlModels);

                    //数据库中也不存在 则重新爬取
                    if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
                        crawlerAndSaveUrl(searchMovieText, "unreadmovie",proxyIp,proxyPort);
                    }

                    redisTemplate.opsForHash().putIfAbsent("unreadmovie", searchMovieText, movieNameAndUrlModels);
                    redisTemplate.expire(searchMovieText, 60, TimeUnit.SECONDS);
                    return movieNameAndUrlModels;
                } else {
                    return movieNameAndUrlModels;
                }

            case "x":
//                  从 社区动力
//               从redis 中拿
                movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_sumsu", searchMovieText);
                if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
                    //从数据库里拿
                    movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_sumsu", searchMovieText);

                    //数据库中也不存在 则重新爬取
                    if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
                        crawlerAndSaveUrl(searchMovieText, "sumsu",proxyIp,proxyPort);
                    }

                    redisTemplate.opsForHash().putIfAbsent("sumsu", searchMovieText, movieNameAndUrlModels);
                    redisTemplate.expire(searchMovieText, 60, TimeUnit.SECONDS);
//                    校验先不做了
//                    invalidUrlCheckingService.checkUrlMethod("url_movie_sumsu", movieNameAndUrlModels)

                    return movieNameAndUrlModels;
                } else {
                    return movieNameAndUrlModels;
                }


            default:
                // 直接从数据库中拿查询全部
                movieNameAndUrlModels.addAll(movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_aidianying", searchMovieText));
                movieNameAndUrlModels.addAll(movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_sumsu", searchMovieText));
                movieNameAndUrlModels.addAll(movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_unread", searchMovieText));
                return movieNameAndUrlModels;

        }

    }

    /**
     * @param
     * @return
     * @Description: 根据待搜索的来源和电影名搜索并存入redis  crawlerNames 对应tableName
     */


    @Async
    public void searchAsyncWord(String searchMovieName, Boolean hasTableName, String crawlerName,String proxyIp,int proxyPort) {

        try {
            if (hasTableName) {
                crawlerAndSaveUrl(searchMovieName, crawlerName,proxyIp,proxyPort);
            } else {
                crawlerAndSaveUrl(searchMovieName, "aidianying",proxyIp,proxyPort);
                crawlerAndSaveUrl(searchMovieName, "unreadmovie",proxyIp,proxyPort);
                crawlerAndSaveUrl(searchMovieName, "sumsu",proxyIp,proxyPort);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * 根据爬取的资源类型返回table 名称
     *
     * @param crawlerName
     * @return
     */
    public String getTableName(String crawlerName) {
        switch (crawlerName) {

            case "aidianying":
                return "url_movie_aidianying";

            case "unreadmovie":
                return "url_movie_unread";
            case "sumsu":
                return "url_movie_sumsu";
            default:
                break;

        }
        return "getTableName";
    }

    /**
     * redis  全部重新爬取
     *
     * @param searchMovieName
     * @param crawlerName
     * @return
     */

    public void crawlerAndSaveUrl(String searchMovieName, String crawlerName,String proxyIp,int proxyPort) {

        try {
            if ("aidianying".equals(crawlerName)) {
                //爱电影 查询并存入数据库 更新redis
                aiDianyingService.saveOrFreshRealMovieUrl(searchMovieName,proxyIp,proxyPort);

            } else if ("unreadmovie".equals(crawlerName)) {
                unReadService.getUnReadCrawlerResult(searchMovieName);
            } else if ("sumsu".equals(crawlerName)) {
                crawlerSumsuService.getSumsuUrl(searchMovieName);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

}



