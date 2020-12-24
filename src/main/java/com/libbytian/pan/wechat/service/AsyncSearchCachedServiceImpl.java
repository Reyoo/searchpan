package com.libbytian.pan.wechat.service;

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
    @Value("${user.unread.weiduyingdan}")
    String unreadUrl;

    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;


    /**
     * 根据不同表示返回不用结果
     *
     * @param searchText
     * @param search
     * @return
     * @throws Exception
     */
    public List<MovieNameAndUrlModel> searchWord(String searchText, String search) throws Exception {

        List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();
        Map<String, List<MovieNameAndUrlModel>> movieNameAndList = new HashMap<>();
        switch (search) {
            case "a":
                //从爱电影获取资源返回aidianying
                movieNameAndList = (Map<String, List<MovieNameAndUrlModel>>) redisTemplate.opsForValue().get("aidianying");
                if (movieNameAndList == null || movieNameAndList.size() == 0) {
                    //从数据库里拿
//                    movieNameAndUrlModels = iMovieNameAndUrlService.findAiDianYingUrl(searchText);
                    movieNameAndUrlModels = iMovieNameAndUrlService.findMovieUrl("url_movie_aidianying","'"+searchText+"'");
//                    redis中没有 、数据库中也没有 、则说明都没有、可以调用异步方法重新爬取 这里需要补一个异步爬取的方法
                    return invalidUrlCheckingService.checkUrlMethod(movieNameAndUrlModels);
                } else {
                    return movieNameAndList.get(searchText);
                }

            case "u":
//                  从未读影单获取资源unreadmovie
                movieNameAndList = (Map<String, List<MovieNameAndUrlModel>>) redisTemplate.opsForValue().get("unreadmovie");
                if (movieNameAndList == null || movieNameAndList.size() == 0) {
                    //从数据库里拿
                    movieNameAndUrlModels = iMovieNameAndUrlService.findUnReadMovieUrl(searchText);
                    return invalidUrlCheckingService.checkUrlMethod(movieNameAndUrlModels);
                } else {
                    return movieNameAndList.get(searchText);
                }

            case "x":
//                  从xxx  获取资源  暂时用未读影单的
                movieNameAndList = (Map<String, List<MovieNameAndUrlModel>>) redisTemplate.opsForValue().get("unreadmovie");
                if (movieNameAndList == null || movieNameAndList.size() == 0) {
                    //从数据库里拿
                    movieNameAndUrlModels = iMovieNameAndUrlService.findUnReadMovieUrl(searchText);
                    return invalidUrlCheckingService.checkUrlMethod(movieNameAndUrlModels);
                } else {
                    return movieNameAndList.get(searchText);
                }

            default:
                movieNameAndList = (Map<String, List<MovieNameAndUrlModel>>) redisTemplate.opsForValue().get("aidianying");
                movieNameAndList.putAll((Map<String, List<MovieNameAndUrlModel>>) redisTemplate.opsForValue().get("unreadmovie"));
                if (movieNameAndList == null || movieNameAndList.size() == 0) {
                    //从数据库里拿
                    movieNameAndUrlModels = iMovieNameAndUrlService.findAiDianYingUrl(searchText);
                    return invalidUrlCheckingService.checkUrlMethod(movieNameAndUrlModels);
                } else {
                    return movieNameAndList.get(searchText);
                }
        }

    }


    /**
     * @param
     * @return
     * @Description: 根据待搜索的来源和电影名搜索并存入redis
     */
    @Async
    public void searchAsyncWord(List<String> crawlerNames, String searchMovieName) throws Exception {


        if (crawlerNames.size() == 0) {
            throw new Exception("要获取的资源名不能为空");
        }


        for (String crawlerName : crawlerNames) {
            //查询redis 中的资源
            Map crawlerNameWithMapModel = new HashMap();

            crawlerNameWithMapModel = (Map<String, List<MovieNameAndUrlModel>>) redisTemplate.opsForValue().get(crawlerName);

            List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();
            //如果redis中存在
            if (crawlerNameWithMapModel != null && crawlerNameWithMapModel.size() > 0) {

                //获取有效链接
                movieNameAndUrlModels = invalidUrlCheckingService.checkUrlMethod((List<MovieNameAndUrlModel>) crawlerNameWithMapModel.get(searchMovieName));
                //将有效连接更新到redis中
                redisTemplate.opsForValue().set(searchMovieName, movieNameAndUrlModels, 60 * 24 * 15, TimeUnit.MINUTES);

            } else {
//                否则不存在

                switch (crawlerName) {

                    case "aidianying":

//                        movieNameAndUrlModels = movieNameAndUrlService.findAiDianYingUrl(searchMovieName);

                        crawlerAndSaveAiDianYingUrl(searchMovieName);

//                        调用爱电影方法
                        break;

                    case "unreadmovie":
//调用未读影单方法
                        movieNameAndUrlModels = movieNameAndUrlService.findUnReadMovieUrl(searchMovieName);
                        break;

                    default:
                        movieNameAndUrlModels.addAll(movieNameAndUrlService.findAiDianYingUrl(searchMovieName));
                        movieNameAndUrlModels.addAll(movieNameAndUrlService.findUnReadMovieUrl(searchMovieName));
                        break;

                }

            }

        }

//        searchText could be unread  loveMovie

        List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();


        /**
         * 逻辑修改
         * 1、先从Redis中获取。
         * 2、如果Redis中有结果，则判断链接是否失效
         * 2.1 、如果链接失效、则删除redis数据
         * 2.1.1 重新获取资源
         * 2.1.2 插入MySQL、同时插入Redis  返回结果。
         * 2.2、如果链接没有失效，则直接返回结果，并异步重新获取资源，更新Redis 及数据库
         */


        movieNameAndUrlModels = (List<MovieNameAndUrlModel>) redisTemplate.opsForValue().get(searchMovieName);
//        2.2、如果链接没有失效，则直接返回结果，并 /异步/重新获取资源，更新Redis 及数据库


    }


    /**
     * 获取爱电影资源
     *
     * @param searchMovieName redis 不存在 去数据库查询的过程
     * @return
     */
    public List<MovieNameAndUrlModel> crawlerAndSaveAiDianYingUrl(String searchMovieName) {
        List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();
        Map<String, List<MovieNameAndUrlModel>> crawlerNameWithMapModel = new HashMap();
        try {
//            从数据库中获取
            movieNameAndUrlModels = movieNameAndUrlService.findAiDianYingUrl(searchMovieName);

//            如果数据库中存在
            if (movieNameAndUrlModels != null && movieNameAndUrlModels.size() > 0) {

//               校验URL
                movieNameAndUrlModels = invalidUrlCheckingService.checkUrlMethod(movieNameAndUrlModels);
                crawlerNameWithMapModel.put(searchMovieName, movieNameAndUrlModels);
                //记录到redis
                redisTemplate.opsForValue().set("AiDianying", crawlerNameWithMapModel, 60 * 24 * 15, TimeUnit.MINUTES);

            } else {
                //如果数据库不存在, 重新爬虫，如果爬取到结果则保存到mysql 中， 如果没爬取到结果将redis设置为null;
                List<MovieNameAndUrlModel> innerMovieList = new ArrayList();

                MovieNameAndUrlModel movieNameAndUrl = normalPageService.getMovieLoopsAiDianying(lxxhUrl + "/?s=" + searchMovieName);

                movieNameAndUrlModels = innerMovieList;
                movieNameAndUrlModels.add(movieNameAndUrl);
                if (movieNameAndUrlModels != null && movieNameAndUrlModels.size() > 0) {
//                        检查url 是否正确
                    movieNameAndUrlModels = invalidUrlCheckingService.checkUrlMethod(movieNameAndUrlModels);
//                        插入更新操作
                    movieNameAndUrlService.addOrUpdateAiDianYingMovieUrls(movieNameAndUrlModels , "url_movie_aidianying");

                    redisTemplate.opsForValue().set("aidianying", crawlerNameWithMapModel.put(searchMovieName,movieNameAndUrlModels), 60 * 24 * 15, TimeUnit.MINUTES);

                } else {
                    redisTemplate.opsForValue().set(searchMovieName, crawlerNameWithMapModel.put(searchMovieName,null), 60 * 24 * 15, TimeUnit.MINUTES);
                }
            }


            return movieNameAndUrlModels;
        } catch (Exception e) {
            log.error(e.getMessage());
            return movieNameAndUrlModels;
        }
    }


//     未读影单
//              List<MovieNameAndUrlModel> unreadUrls = normalPageService.getNormalUrl(unreadUrl + "/?s=" + searchMovieName);
//
//                unreadUrls.stream().forEach(movieNameAndUrl ->
//                        innerMovieList.add(normalPageService.getMoviePanUrl(movieNameAndUrl)));
}



