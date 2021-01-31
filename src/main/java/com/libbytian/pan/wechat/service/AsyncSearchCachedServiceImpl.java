package com.libbytian.pan.wechat.service;

import com.libbytian.pan.system.mapper.MovieNameAndUrlMapper;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    private final MovieNameAndUrlMapper movieNameAndUrlMapper;


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

//                movieNameAndUrlModels = (List<MovieNameAndUrlModel>) redisTemplate.opsForHash().get("aidianying", searchMovieText);

                movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_aidianying", searchMovieText);

                return movieNameAndUrlModels;


            //u 2号大厅
            case "u":
//                  从未读影单获取资源unreadmovie
                movieNameAndUrlModels.addAll(movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_unread", searchMovieText));
                //从数据库里拿
                movieNameAndUrlModels.addAll(movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_sumsu", searchMovieText));

                return movieNameAndUrlModels;


            case "a":
//                  从小悠家获取资源
//                movieNameAndUrlModels = (List<MovieNameAndUrlModel>) redisTemplate.opsForHash().get("xiaoyoumovie", searchMovieText);
                movieNameAndUrlModels = movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_xiaoyou", searchMovieText);

                return movieNameAndUrlModels;


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



