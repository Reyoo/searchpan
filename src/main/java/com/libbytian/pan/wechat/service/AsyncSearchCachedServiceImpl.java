package com.libbytian.pan.wechat.service;

import com.libbytian.pan.wechat.model.MovieNameAndUrlModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
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
@Service
@Slf4j
public class AsyncSearchCachedServiceImpl {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    NormalPageService normalPageService;

    @Value("${user.unread.weiduyingdan}")
    String unreadUrl;
    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;

    /**
     *
     * @param searchText
     * @Description: 根据待搜索关键词 搜索并存入redis
     *
     * @return
     */
    @Async
    public List<MovieNameAndUrlModel> searchWord(String searchText){
        try {

            //先从redis 中根据 搜索影片名 有结果直接返回
            List<MovieNameAndUrlModel> realMovieList = new ArrayList();
//            if(redisTemplate.opsForList().size(searchText)>0){
//                realMovieList = (List<MovieNameAndUrlModel>) redisTemplate.opsForList().rightPop(searchText);
//            }else {

               LocalTime begin = LocalTime.now();
                //查询第一个链接
                List<MovieNameAndUrlModel> innerMovieList = new ArrayList();
                List<MovieNameAndUrlModel> movieNameAndUrls =(List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(unreadUrl+"/?s="+searchText).get("data");

                movieNameAndUrls.stream().forEach( movieNameAndUrl ->
                        innerMovieList.add(normalPageService.getMoviePanUrl(movieNameAndUrl)));

                //查询第二个链接
                List<MovieNameAndUrlModel> movieNameAndUrls1 =(List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(lxxhUrl+"/?s="+searchText).get("data");
                movieNameAndUrls1.stream().forEach( movieNameAndUrl ->
                        innerMovieList.add(normalPageService.getMoviePanUrl2(movieNameAndUrl)));

                realMovieList = innerMovieList;
                //没有结果第一次先去查询
                //redisTemplate.opsForList().rightPushAll(inMessage.getContent(), realMovieList);
                redisTemplate.opsForValue().set(searchText,realMovieList,60 * 24 * 7 , TimeUnit.MINUTES);

                LocalTime end = LocalTime.now();
                Duration duration = Duration.between(begin, end);
                log.info("Duration: " + duration);

//            }
            return realMovieList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
