package com.libbytian.pan.wechat.service;

import com.libbytian.pan.crawler.service.AsyncTask;
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

    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    private final AsyncTask asyncTask;




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
            //a 一号大厅
            case "x":
                //从爱电影获取资源返回aidianying
//                先从redis中获取

                movieNameAndUrlModels = (List<MovieNameAndUrlModel>) redisTemplate.opsForHash().get("aidianying", searchMovieText);

                for (MovieNameAndUrlModel movieNameAndUrlModel : movieNameAndUrlModels) {
                    if (!asyncTask.exist(movieNameAndUrlModel.getWangPanUrl())){
                        movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_aidianying", searchMovieText);

                        for (MovieNameAndUrlModel model : movieNameAndUrlModels) {
                            if (!asyncTask.exist(model.getWangPanUrl())){
                                redisTemplate.opsForHash().put("aidianying", searchMovieText, movieNameAndUrlModels);
                                redisTemplate.expire(searchMovieText, 60, TimeUnit.SECONDS);
                            }
                        }

                        return movieNameAndUrlModels;
                    }else {
                        return movieNameAndUrlModels;
                    }

                }


//                if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
////数据库中没有从 mysql 中获取
//                    movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_aidianying", searchMovieText);
//                    redisTemplate.opsForHash().put("aidianying", searchMovieText, movieNameAndUrlModels);
//                    redisTemplate.expire(searchMovieText, 60, TimeUnit.SECONDS);
//                    return movieNameAndUrlModels;
//                } else {
//                    return movieNameAndUrlModels;
//                }


                //u 2号大厅
            case "u":
//                  从未读影单获取资源unreadmovie
                List<MovieNameAndUrlModel> movieNameAndUrlModels1 = new ArrayList<>();
                List<MovieNameAndUrlModel> movieNameAndUrlModels2 = new ArrayList<>();

                movieNameAndUrlModels1 = (List<MovieNameAndUrlModel>) redisTemplate.opsForHash().get("unreadmovie", searchMovieText);
                movieNameAndUrlModels2 = (List<MovieNameAndUrlModel>) redisTemplate.opsForHash().get("sumsu", searchMovieText);


                if (movieNameAndUrlModels1 == null || movieNameAndUrlModels1.size() == 0) {
                    //从数据库里拿
                    movieNameAndUrlModels1 = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_unread", searchMovieText);

                    redisTemplate.opsForHash().put("unreadmovie", searchMovieText, movieNameAndUrlModels1);
                    redisTemplate.expire(searchMovieText, 60, TimeUnit.SECONDS);
                }
                if (movieNameAndUrlModels2 == null || movieNameAndUrlModels2.size() == 0){

                    //从数据库里拿
                    movieNameAndUrlModels2 = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_sumsu", searchMovieText);
                    //数据库中也不存在 则重新爬取
                    redisTemplate.opsForHash().put("sumsu", searchMovieText, movieNameAndUrlModels2);
                    redisTemplate.expire(searchMovieText, 60, TimeUnit.SECONDS);

                }


                movieNameAndUrlModels.addAll(movieNameAndUrlModels1);
                movieNameAndUrlModels.addAll(movieNameAndUrlModels2);

                return movieNameAndUrlModels;



//                else {
//                    return movieNameAndUrlModels;
//                }

                //x 三号大厅
//            case "x":
////                  从 社区动力
////               从redis 中拿
//
//                movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_sumsu", searchMovieText);
//                if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
//                    //从数据库里拿
//                    movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_sumsu", searchMovieText);
//                    //数据库中也不存在 则重新爬取
//                    redisTemplate.opsForHash().put("sumsu", searchMovieText, movieNameAndUrlModels);
//                    redisTemplate.expire(searchMovieText, 60, TimeUnit.SECONDS);
//
//                    return movieNameAndUrlModels;
//                } else {
//                    return movieNameAndUrlModels;
//                }


            case "a":
//                  从小悠家获取资源

                movieNameAndUrlModels = (List<MovieNameAndUrlModel>) redisTemplate.opsForHash().get("xiaoyoumovie", searchMovieText);

                if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
                    //从数据库里拿
                    movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_xiaoyou", searchMovieText);

                    redisTemplate.opsForHash().put("xiaoyoumovie", searchMovieText, movieNameAndUrlModels);
                    redisTemplate.expire(searchMovieText, 60, TimeUnit.SECONDS);
                    return movieNameAndUrlModels;
                } else {
                    return movieNameAndUrlModels;
                }

            default:
                return movieNameAndUrlModels;
        }

    }

    /**
     * @param
     * @return
     * @Description: 根据待搜索的来源和电影名搜索并存入redis  crawlerNames 对应tableName
     */

}



