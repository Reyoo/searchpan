package com.libbytian.pan.wechat.service;

import com.libbytian.pan.findmovie.aidianying.IFindMovieInAiDianYing;
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
public class AsyncSearchCachedComponent {


    private final IFindMovieInAiDianYing iFindMovieInAiDianYing;
    private final IFindMovieInSumsu iFindMovieInSumsu;
    private final IFindMovieInUnread iFindMovieInUnread;
    private final IFindMovieInXiaoyou iFindMovieInXiaoyou;
    private final IFindMovieInYoujiang IFindMovieInYoujiang;


    /**
     * 根据不同表示返回不用结果
     *
     * @param searchMovieText
     * @param search
     * @return
     * @throws Exception
     */


    public List<MovieNameAndUrlModel> searchWord(String searchMovieText, String search) throws Exception {
        switch (search) {
            //a 一号大厅
            case "a":
                List<MovieNameAndUrlModel> listA = new ArrayList<>();
                //添加悠酱
                listA.addAll(IFindMovieInYoujiang.findMovieUrl(searchMovieText));
                //添加未读影单
                listA.addAll(iFindMovieInUnread.findMovieUrl(searchMovieText));
                return listA;
            //u 2号大厅
            case "u":
                //爱电影
                return iFindMovieInAiDianYing.findMovieUrl(searchMovieText);
            //x 3号大厅
            case "x":
                List<MovieNameAndUrlModel> listC = new ArrayList<>();
                //添加小悠
                listC.addAll(iFindMovieInXiaoyou.findMovieUrl(searchMovieText));
                //添加社区动力
                listC.addAll(iFindMovieInSumsu.findMovieUrl(searchMovieText));
                return listC;
            default:
                return new ArrayList<MovieNameAndUrlModel>();
        }
    }
}



