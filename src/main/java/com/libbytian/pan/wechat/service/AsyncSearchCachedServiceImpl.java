package com.libbytian.pan.wechat.service;

import com.libbytian.pan.system.mapper.MovieNameAndUrlMapper;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import com.libbytian.pan.system.service.impl.InvalidUrlCheckingService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class AsyncSearchCachedServiceImpl {


    private final RedisTemplate redisTemplate;

    private final InvalidUrlCheckingService invalidUrlCheckingService;

    private final IMovieNameAndUrlService movieNameAndUrlService;

    private final NormalPageService normalPageService;

    private final IMovieNameAndUrlService iMovieNameAndUrlService;

    private final MovieNameAndUrlMapper movieNameAndUrlMapper;
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
                movieNameAndUrlModels = (List<MovieNameAndUrlModel>) redisTemplate.opsForHash().get("aidianying", searchMovieText);

                if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
                    //从数据库里拿
//                    movieNameAndUrlModels = iMovieNameAndUrlService.findAiDianYingUrl(searchText);
                    movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_aidianying", searchMovieText);
//                    redis中没有 、数据库中也没有 、则说明都没有、可以调用异步方法重新爬取 这里需要补一个异步爬取的方法
                    List<MovieNameAndUrlModel> movieNameAndUrlModelList = invalidUrlCheckingService.checkUrlMethod("url_movie_aidianying", movieNameAndUrlModels);
                    //更新一波Redis
                    redisTemplate.opsForHash().putIfAbsent("aidianying", searchMovieText, movieNameAndUrlModelList);
                    return movieNameAndUrlModelList;
                } else {
                    return movieNameAndUrlModels;
                }

            case "u":
//                  从未读影单获取资源unreadmovie

                movieNameAndUrlModels = (List<MovieNameAndUrlModel>) redisTemplate.opsForHash().get("unreadmovie", searchMovieText);

                if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
                    //从数据库里拿
                    movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_unread", "'" + searchMovieText + "'");
                    redisTemplate.opsForHash().putIfAbsent("unreadmovie", searchMovieText, movieNameAndUrlModels);
                    return invalidUrlCheckingService.checkUrlMethod("url_movie_unread", movieNameAndUrlModels);
                } else {
                    return movieNameAndUrlModels;
                }

            case "x":
//                  从xxx  获取资源  暂时用未读影单的
                movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("XXXX", searchMovieText);
                if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
                    //从数据库里拿
                    movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_unreadxx", "'" + searchMovieText + "'");
                    return invalidUrlCheckingService.checkUrlMethod("url_movie_unreadxx", movieNameAndUrlModels);
                } else {
                    return movieNameAndUrlModels;
                }

            default:
                movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_aidianying", searchMovieText);
                movieNameAndUrlModels.addAll(movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_aidianying", searchMovieText));
                if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
                    //从数据库不同表里拿
                    movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("diffrent-table", "'" + searchMovieText + "'");
                    return invalidUrlCheckingService.checkUrlMethod("diffrent-table", movieNameAndUrlModels);
                } else {
                    return movieNameAndUrlModels;
                }
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
                List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();
                //获取有效链接
                movieNameAndUrlModels = invalidUrlCheckingService.checkUrlMethod(getTableName(crawlerName), movieNameAndMovieList);
                //将有效连接更新到redis中

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

                    default:
                        crawlerAndSaveUrl(searchMovieName, "url_movie_aidianying", crawlerName);
                        crawlerAndSaveUrl(searchMovieName, "url_movie_unread", crawlerName);
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

            default:
                break;

        }
        return null;
    }

    /**
     * redis 不存在 去数据库查询的过程 同时更新redis
     *
     * @param searchMovieName
     * @param tableName
     * @param crawlerName
     * @return
     */

    public List<MovieNameAndUrlModel> crawlerAndSaveUrl(String searchMovieName, String tableName, String crawlerName) {
        List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();

        try {
//            从数据库中获取
            movieNameAndUrlModels = movieNameAndUrlService.findMovieUrl(tableName, searchMovieName);
//            如果数据库中存在
            if (movieNameAndUrlModels != null && movieNameAndUrlModels.size() > 0) {
//               校验URL
                movieNameAndUrlModels = invalidUrlCheckingService.checkUrlMethod(tableName, movieNameAndUrlModels);
                //记录到redis
                redisTemplate.opsForHash().putIfAbsent(crawlerName, searchMovieName, movieNameAndUrlModels);

            } else {
                //如果数据库不存在, 重新爬虫，如果爬取到结果则保存到mysql 中， 如果没爬取到结果将redis设置为null;
                List<MovieNameAndUrlModel> innerMovieList = new ArrayList();
                MovieNameAndUrlModel movieNameAndUrl = null;

                if ("aidianying".equals(crawlerName)) {
                    movieNameAndUrl = normalPageService.getMovieLoopsAiDianying(lxxhUrl + "/?s=" + searchMovieName);
                    redisTemplate.opsForHash().putIfAbsent(crawlerName, searchMovieName, new ArrayList().add(movieNameAndUrl));
                } else if ("unreadmovie".equals(crawlerName)) {
                    List<MovieNameAndUrlModel> unreadUrls = normalPageService.getNormalUrl(unreadUrl + "/?s=" + searchMovieName);
                    unreadUrls.stream().forEach(innermovieNameAndUrl ->
                            innerMovieList.add(normalPageService.getMoviePanUrl(innermovieNameAndUrl)));
                    redisTemplate.opsForHash().putIfAbsent(crawlerName, searchMovieName, new ArrayList().add(innerMovieList));

                } else {
                    movieNameAndUrl = normalPageService.getMovieLoopsAiDianying(lxxhUrl + "/?s=" + searchMovieName);
                    List<MovieNameAndUrlModel> unreadUrls = normalPageService.getNormalUrl(unreadUrl + "/?s=" + searchMovieName);
                    unreadUrls.stream().forEach(innermovieNameAndUrl ->
                            innerMovieList.add(normalPageService.getMoviePanUrl(innermovieNameAndUrl)));
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
                } else {
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



