package com.libbytian.pan.wechat.service;

import com.libbytian.pan.findmovie.aidianying.IFindMovieInAiDianYing;
import com.libbytian.pan.findmovie.sumsu.IFindMovieInSumsu;
import com.libbytian.pan.findmovie.unread.IFindMovieInUnread;
import com.libbytian.pan.findmovie.xiaoyou.IFindMovieInXiaoyou;
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
            case "x":
                return iFindMovieInAiDianYing.findMovieUrl(searchMovieText);
            //u 2号大厅
            case "u":
            List<MovieNameAndUrlModel> movieNameAndUrlModels = new ArrayList<>();
                movieNameAndUrlModels.addAll(iFindMovieInUnread.findMovieUrl(searchMovieText));
                //从数据库里拿
                movieNameAndUrlModels.addAll(iFindMovieInSumsu.findMovieUrl(searchMovieText));
                return movieNameAndUrlModels;
            case "a":
//                  从小悠家获取资源
                return iFindMovieInXiaoyou.findMovieUrl(searchMovieText);

            default:
                return new ArrayList<MovieNameAndUrlModel>();
        }
    }
}



