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
    public List<MovieNameAndUrlModel> searchWord(String searchMovieText, String search) throws Exception {

        List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();

        switch (search) {
            case "a":
                //从爱电影获取资源返回aidianying
                log.info("--------------------------------------------");
                movieNameAndUrlModels = (List<MovieNameAndUrlModel>) redisTemplate.opsForHash().get("aidianying", searchMovieText);
                log.info("--------------------------------------------");
                if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {

                    movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_aidianying", searchMovieText);
//                    redis中没有 、数据库中也没有 、则说明都没有、可以调用异步方法重新爬取 这里需要补一个异步爬取的方法

//                    校验影响速度 暂时放过
//                    List<MovieNameAndUrlModel> movieNameAndUrlModelList = invalidUrlCheckingService.checkUrlMethod("url_movie_aidianying", movieNameAndUrlModels);
                    //更新一波Redis
                    redisTemplate.opsForHash().putIfAbsent("aidianying", searchMovieText, movieNameAndUrlModels);
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
                    redisTemplate.opsForHash().putIfAbsent("unreadmovie", searchMovieText, movieNameAndUrlModels);
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
                    redisTemplate.opsForHash().putIfAbsent("sumsu", searchMovieText, movieNameAndUrlModels);
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
    public void searchAsyncWord(List<String> crawlerNames, String searchMovieName) throws Exception {

        if (crawlerNames.size() == 0) {
            throw new Exception("要获取的资源名不能为空");
        }

        for (String crawlerName : crawlerNames) {
            //查询redis 中的资源
            List<MovieNameAndUrlModel> movieNameAndMovieList = new ArrayList<>();
            movieNameAndMovieList = (List<MovieNameAndUrlModel>) redisTemplate.opsForHash().get(crawlerName, searchMovieName);

            //如果redis中存在
            if (movieNameAndMovieList != null && movieNameAndMovieList.size() > 0) {
                List<MovieNameAndUrlModel> movieNameAndUrlModels = invalidUrlCheckingService.checkUrlMethod(getTableName(crawlerName), movieNameAndMovieList);
                //将有效连接更新到redis中

                movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndMovieList,getTableName(crawlerName));
                redisTemplate.opsForHash().putIfAbsent(crawlerName, searchMovieName, movieNameAndMovieList);



            } else {
//                否则Redis中不存在 //重新爬虫 存入到mysql和redis中
                switch (crawlerName) {

                    case "aidianying":
                        crawlerAndSaveUrl(searchMovieName, "url_movie_aidianying", crawlerName);
                        break;

                    case "unreadmovie":
                        crawlerAndSaveUrl(searchMovieName, "url_movie_unread", crawlerName);
                        break;

                    case "sumsu":
                        crawlerAndSaveUrl(searchMovieName, "url_movie_sumsu", crawlerName);
                        break;
                    default:
                        crawlerAndSaveUrl(searchMovieName, "url_movie_aidianying", crawlerName);
                        crawlerAndSaveUrl(searchMovieName, "url_movie_unread", crawlerName);
                        crawlerAndSaveUrl(searchMovieName, "url_movie_sumsu", crawlerName);
                        break;

                }
            }
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
     * redis 不存在 去数据库查询的过程 同时更新redis
     *
     * @param searchMovieName
     * @param tableName
     * @param crawlerName
     * @return
     */

    @Async
    public List<MovieNameAndUrlModel> crawlerAndSaveUrl(String searchMovieName, String tableName, String crawlerName) {
        List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();

        try {
//            从数据库中获取
            movieNameAndUrlModels = movieNameAndUrlService.findMovieUrl(tableName, searchMovieName);
//            如果数据库中存在
            if (movieNameAndUrlModels != null && movieNameAndUrlModels.size() > 0) {
//               校验URL 摘掉 URL 校验
//                movieNameAndUrlModels = invalidUrlCheckingService.checkUrlMethod(tableName, movieNameAndUrlModels);
                movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModels,tableName);

                //记录到redis
                redisTemplate.opsForHash().putIfAbsent(crawlerName, searchMovieName, movieNameAndUrlModels);

            } else {
                //如果数据库不存在, 重新爬虫，如果爬取到结果则保存到mysql 中， 如果没爬取到结果将redis设置为null;
                List<MovieNameAndUrlModel> innerMovieList = new ArrayList();
                MovieNameAndUrlModel movieNameAndUrl = null;

                if ("aidianying".equals(crawlerName)) {
                    //爱电影 查询并存入数据库 更新redis
                    List<MovieNameAndUrlModel> aiDianyingList = aiDianyingService.saveOrFreshRealMovieUrl(searchMovieName);

                    redisTemplate.opsForHash().putIfAbsent(crawlerName, searchMovieName, aiDianyingList);
                    innerMovieList = aiDianyingList;

                } else if ("unreadmovie".equals(crawlerName)) {
                    List<MovieNameAndUrlModel> unreadUrls = unReadService.getUnReadCrawlerResult(searchMovieName);
                    innerMovieList = unreadUrls;

                } else if ("sumsu".equals(crawlerName)) {
                    innerMovieList = crawlerSumsuService.getSumsuUrl(searchMovieName);
                    redisTemplate.opsForHash().putIfAbsent(crawlerName, searchMovieName, innerMovieList);

                } else {

                    List<MovieNameAndUrlModel> aiDianyingList = aiDianyingService.saveOrFreshRealMovieUrl(searchMovieName);
                    //未读影单
                    List<MovieNameAndUrlModel> unreadUrls = unReadService.getUnReadCrawlerResult(searchMovieName);

                    //社区动力
                    List<MovieNameAndUrlModel> sumsuMovieList = new ArrayList<>();
                    sumsuMovieList = crawlerSumsuService.getSumsuUrl(searchMovieName);
                    redisTemplate.opsForHash().putIfAbsent("sumsu", searchMovieName, sumsuMovieList);

                    innerMovieList.addAll(aiDianyingList);
                    innerMovieList.addAll(unreadUrls);
                    innerMovieList.addAll(sumsuMovieList);
                }


                movieNameAndUrlModels = innerMovieList;

                if (movieNameAndUrl != null) {
                    movieNameAndUrlModels.add(movieNameAndUrl);
                }

                if (movieNameAndUrlModels != null && movieNameAndUrlModels.size() > 0) {
//                        检查url 是否正确
                    movieNameAndUrlModels = invalidUrlCheckingService.checkUrlMethod(tableName, movieNameAndUrlModels);
//                        插入更新操作
                    movieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModels, tableName);
                    //记录到redis
                    redisTemplate.opsForHash().putIfAbsent(crawlerName, searchMovieName, movieNameAndUrlModels);
                }

            }

            return movieNameAndUrlModels;
        } catch (Exception e) {
            log.error(e.getMessage());
            return movieNameAndUrlModels;
        }
    }


}



