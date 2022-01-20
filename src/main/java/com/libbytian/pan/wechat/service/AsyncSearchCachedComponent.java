package com.libbytian.pan.wechat.service;

import com.libbytian.pan.findmovie.aidianying.IFindMovieInAiDianYing;
import com.libbytian.pan.findmovie.lili.IFindMovieInLiLi;
import com.libbytian.pan.findmovie.sumsu.IFindMovieInSumsu;
import com.libbytian.pan.findmovie.unread.IFindMovieInUnread;
import com.libbytian.pan.findmovie.xiaoyou.IFindMovieInXiaoyou;
import com.libbytian.pan.findmovie.youjiang.IFindMovieInYoujiang;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@EnableAsync
public class AsyncSearchCachedComponent {


    private final IFindMovieInAiDianYing iFindMovieInAiDianYing;
    private final IFindMovieInSumsu iFindMovieInSumsu;
    private final IFindMovieInUnread iFindMovieInUnread;
    private final IFindMovieInXiaoyou iFindMovieInXiaoyou;
    private final IFindMovieInYoujiang iFindMovieInYoujiang;
    private final IFindMovieInLiLi iFindMovieInLiLi;


    /**
     * 根据不同表示返回不用结果
     *
     * @param searchMovieText
     * @param search
     * @return
     * @throws Exception
     */
    public Map<String, List<MovieNameAndUrlModel>> searchWord(String searchMovieText, String search) throws Exception {

        switch (search) {
            //a 一号大厅 小悠
            case "a":
                Map<String, List<MovieNameAndUrlModel>> collectLiLi = iFindMovieInLiLi.findMovieUrl(searchMovieText).stream().collect(Collectors.groupingBy(MovieNameAndUrlModel::getMovieName));
                return collectLiLi;
            //u 二号大厅 莉莉
            case "u":
                Map<String, List<MovieNameAndUrlModel>> collectXiaoYou = iFindMovieInXiaoyou.findMovieUrl(searchMovieText).stream().collect(Collectors.groupingBy(MovieNameAndUrlModel::getMovieName));
                return collectXiaoYou;

            //x 3号大厅
            case "x":
                Map<String,  List<MovieNameAndUrlModel>> combineResultMap = new HashMap<>();
                Map<String, List<MovieNameAndUrlModel>> collectUnread = iFindMovieInUnread.findMovieUrl(searchMovieText).stream().collect(Collectors.groupingBy(MovieNameAndUrlModel::getMovieName));
                Map<String, List<MovieNameAndUrlModel>> collectAiDianYing = iFindMovieInAiDianYing.findMovieUrl(searchMovieText).stream().collect(Collectors.groupingBy(MovieNameAndUrlModel::getMovieName));
                //添加未读影单
                combineResultMap.putAll(collectUnread);
                //添加爱电影
                combineResultMap.putAll(collectAiDianYing);
            return combineResultMap;
            default:
                return new HashMap<>();

        }


        /**
         * 备份使用
         */
//    public List<MovieNameAndUrlModel> searchWord(String searchMovieText, String search) throws Exception {
//
//        switch (search) {
//            //a 一号大厅
//            case "a":
//                List<MovieNameAndUrlModel> listA = new ArrayList<>();
//                //添加 莉莉
//                List<MovieNameAndUrlModel> movieUrl = iFindMovieInLiLi.findMovieUrl(searchMovieText);
//                movieUrl.stream().forEach(movieNameAndUrlModel -> movieNameAndUrlModel.setMovieName(movieNameAndUrlModel.getMovieName()+movieNameAndUrlModel.getTitleName()));
//                listA.addAll(movieUrl);
//
//                return listA;
//            //u 2号大厅
//            case "u":
//                List<MovieNameAndUrlModel> listB = new ArrayList<>();
//                //添加小悠
//                List<MovieNameAndUrlModel> xiaoYouMovieUrl = iFindMovieInXiaoyou.findMovieUrl(searchMovieText);
//                xiaoYouMovieUrl.stream().forEach(movieNameAndUrlModel -> movieNameAndUrlModel.setMovieName(movieNameAndUrlModel.getMovieName()+movieNameAndUrlModel.getTitleName()));
//                listB.addAll(xiaoYouMovieUrl);
//
//                return listB;
//            //x 3号大厅
//            case "x":
//                List<MovieNameAndUrlModel> listC = new ArrayList<>();
//                //添加未读影单
//                listC.addAll(iFindMovieInUnread.findMovieUrl(searchMovieText));
//                //爱电影
//                listC.addAll(iFindMovieInAiDianYing.findMovieUrl(searchMovieText));
//                return listC;
//            default:
//                return new ArrayList<MovieNameAndUrlModel>();
//        }
    }
}



