package com.libbytian.pan.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.system.service.impl
 * @ClassName: InvalidUrlCheckingService
 * @Author: sun71
 * @Description: 失效链接检测业务类
 * @Date: 2020/12/5 21:17
 * @Version: 1.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class InvalidUrlCheckingService {


    private final IMovieNameAndUrlService movieNameAndUrlService;

    /**
     * 判断是否失效、失效则数据库中删除
     * @param movieNameAndUrlModels
     * @return
     * @throws Exception
     */
    public List<MovieNameAndUrlModel> checkUrlMethod(String tableName ,List<MovieNameAndUrlModel> movieNameAndUrlModels) throws Exception {

        List<MovieNameAndUrlModel> couldBeFindUrls = new ArrayList<>();
        if (movieNameAndUrlModels == null || movieNameAndUrlModels.size() == 0) {
            log.info("入参 电影列表为空 未找到资源");
            return couldBeFindUrls;
        } else {

            for (MovieNameAndUrlModel movieNameAndUrlModel : movieNameAndUrlModels) {

                String wangPanUrl = movieNameAndUrlModel.getWangPanUrl();
                if(StrUtil.isBlank(wangPanUrl)){
                    continue;
                }
                Document document = Jsoup.connect(wangPanUrl).get();
                String title = document.title();
                //获取html中的标题
                log.info("title--> :" + title);
                if (!"百度网盘-链接不存在".contains(title) || !"页面不存在".contains(title)) {
                    //Redis 暂时不做处理
                    couldBeFindUrls.add(movieNameAndUrlModel);
                }else {
                    movieNameAndUrlService.dropMovieUrl(tableName,movieNameAndUrlModel);
                }
            }
            return couldBeFindUrls;
        }
    }



    public boolean checkUrlByUrlStr(String url) throws Exception{

        //从URL加载HTML
        Document document = Jsoup.connect(url).get();
        String title = document.title();
        //获取html中的标题
        System.out.println("title :"+title);
        if("百度网盘-链接不存在".contains(title)||"页面不存在".contains(title)){
            return true;
        }
        return false;
    }




}
