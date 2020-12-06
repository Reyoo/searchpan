package com.libbytian.pan.wechat.service;

import cn.hutool.core.util.StrUtil;
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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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



    @Value("${user.unread.weiduyingdan}")
    String unreadUrl;
    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;

    /**
     * @param searchText
     * @return
     * @Description: 根据待搜索关键词 搜索并存入redis
     */
    @Async
    public List<MovieNameAndUrlModel> searchWord(String searchText) {
        List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();

        try {

            /**
             * 逻辑修改
             * 1、先从Redis中获取。
             * 2、如果Redis中有结果，则判断链接是否失效
             * 2.1 、如果链接失效、则删除redis数据
             * 2.1.1 重新获取资源
             * 2.1.2 插入MySQL、同时插入Redis  返回结果。
             * 2.2、如果链接没有失效，则直接返回结果，并异步重新获取资源，更新Redis 及数据库
             */
            movieNameAndUrlModels = (List<MovieNameAndUrlModel>) redisTemplate.opsForValue().get(searchText);
//        2.2、如果链接没有失效，则直接返回结果，并 /异步/重新获取资源，更新Redis 及数据库
            if (movieNameAndUrlModels != null && movieNameAndUrlModels.size() > 0) {
                List<MovieNameAndUrlModel> checkedMovieNameAndUrlModels = new ArrayList<>();
                for(MovieNameAndUrlModel movieNameAndUrlModel : movieNameAndUrlModels){
                    //如果网盘不存在则删除mysql 中数据
                    if(StrUtil.isBlank(movieNameAndUrlModel.getWangPanUrl())){
                        continue;
                    }
                    if(invalidUrlCheckingService.checkUrlMethod(movieNameAndUrlModel.getWangPanUrl())){
                        //从数据库中删除掉失效连接
                       int delNum =  movieNameAndUrlService.dropMovieUrl(movieNameAndUrlModel);
                        System.out.println(movieNameAndUrlModel.getMovieName());

                    }else {
                        //循环校验 放入新的list中
                        checkedMovieNameAndUrlModels.add(movieNameAndUrlModel);
                    }
                    movieNameAndUrlModels = checkedMovieNameAndUrlModels;
                }

                //更新redis
                if(redisTemplate.delete(searchText)){
                    redisTemplate.opsForValue().set(searchText, movieNameAndUrlModels, 60 * 24 * 15, TimeUnit.MINUTES);
                }

                //由于redis数据库设计、此处直接返回不做失效链接判断
                return movieNameAndUrlModels;

            } else {
                //2.1.1  如果没有数据，从数据库中获取数据
                //模糊查询是否放过？
                movieNameAndUrlModels = movieNameAndUrlService.findMovieUrl(searchText);
                if (movieNameAndUrlModels != null && movieNameAndUrlModels.size() > 0) {
                    //记录到redis
                    redisTemplate.opsForValue().set(searchText, movieNameAndUrlModels, 60 * 24 * 15, TimeUnit.MINUTES);
                    //返回结果
                    return movieNameAndUrlModels;
                } else {
                    //重新爬虫，如果爬取到结果则保存到mysql 中， 如果没爬取到结果将redis设置为null;
                    List<MovieNameAndUrlModel> innerMovieList = new ArrayList();
                    List<MovieNameAndUrlModel> unreadUrls = (List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(unreadUrl + "/?s=" + searchText).get("data");

                    unreadUrls.stream().forEach(movieNameAndUrl ->
                            innerMovieList.add(normalPageService.getMoviePanUrl(movieNameAndUrl)));

                    //查询第二个链接
                    List<MovieNameAndUrlModel> lxxhUrls = (List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(lxxhUrl + "/?s=" + searchText).get("data");
                    lxxhUrls.stream().forEach(movieNameAndUrl ->
                            innerMovieList.add(normalPageService.getMoviePanUrl2(movieNameAndUrl)));

                    movieNameAndUrlModels = innerMovieList;

                    if (movieNameAndUrlModels != null && movieNameAndUrlModels.size() > 0) {
                        movieNameAndUrlService.addMovieUrl(movieNameAndUrlModels);
                        redisTemplate.opsForValue().set(searchText, movieNameAndUrlModels, 60 * 24 * 15, TimeUnit.MINUTES);
                        return movieNameAndUrlModels;
                    } else {
                        redisTemplate.opsForValue().set(searchText, null, 60 * 24 * 15, TimeUnit.MINUTES);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return movieNameAndUrlModels;
        }
        return movieNameAndUrlModels;
    }

}

//            //先从redis 中根据 搜索影片名 有结果直接返回
//            List<MovieNameAndUrlModel> realMovieList = new ArrayList();
//            List<MovieNameAndUrlModel>  findList = (List<MovieNameAndUrlModel>)redisTemplate.opsForValue().get(searchText);
//            if(findList!=null && findList.size()>0 ){
//                return findList;
//            }else{
//                LocalTime begin = LocalTime.now();
//                //查询第一个链接
//                List<MovieNameAndUrlModel> innerMovieList = new ArrayList();
//                List<MovieNameAndUrlModel> movieNameAndUrls = (List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(unreadUrl + "/?s=" + searchText).get("data");
//
//                movieNameAndUrls.stream().forEach(movieNameAndUrl ->
//                        innerMovieList.add(normalPageService.getMoviePanUrl(movieNameAndUrl)));
//
//                //查询第二个链接
//                List<MovieNameAndUrlModel> movieNameAndUrls1 = (List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(lxxhUrl + "/?s=" + searchText).get("data");
//                movieNameAndUrls1.stream().forEach(movieNameAndUrl ->
//                        innerMovieList.add(normalPageService.getMoviePanUrl2(movieNameAndUrl)));
//
//                realMovieList = innerMovieList;
//                //没有结果第一次先去查询
//                //redisTemplate.opsForList().rightPushAll(inMessage.getContent(), realMovieList);
//                redisTemplate.opsForValue().set(searchText, realMovieList, 60 * 24 * 15, TimeUnit.MINUTES);
//
//                LocalTime end = LocalTime.now();
//                Duration duration = Duration.between(begin, end);
//                log.info("Duration: " + duration);
//
//                return realMovieList;
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//


