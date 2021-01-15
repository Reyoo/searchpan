package com.libbytian.pan.crawler.service;

import cn.hutool.core.util.StrUtil;
import com.libbytian.pan.crawler.service.sumsu.CrawlerSumsuService;
import com.libbytian.pan.crawler.service.unread.UnReadService;
import com.libbytian.pan.proxy.service.GetProxyService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import com.libbytian.pan.wechat.service.NormalPageService;
import com.libbytian.pan.crawler.service.aidianying.AiDianyingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.crawler.service
 * @ClassName: AsyncTask
 * @Author: sun71
 * @Description: 线程池任务类
 * @Date: 2020/12/13 12:00
 * @Version: 1.0
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AsyncTask {


    private final AiDianyingService aiDianyingService;
    private final UnReadService unReadService;
    private final GetProxyService getProxyService;
    private final CrawlerSumsuService crawlerSumsuService;

    private final IMovieNameAndUrlService iMovieNameAndUrlService;




    @Async("crawler-Executor")
    public void crawlerMovie(String searchName){
        //设置代理IP PORT
        String ipAndPort = getProxyService.getProxyIpFromRemote();
//                String ipAndPort = getProxyService.getProxyIp();
        String proxyIp = ipAndPort.split(":")[0];
        int proxyPort = Integer.valueOf(ipAndPort.split(":")[1]);

        aiDianyingService.saveOrFreshRealMovieUrl(searchName, proxyIp, proxyPort);
        unReadService.getUnReadCrawlerResult(searchName, proxyIp, proxyPort);
        crawlerSumsuService.getSumsuUrl(searchName,proxyIp,proxyPort);
    }





    /**
     * 爱电影
     * @param url
     * @param num
     * @param s
     * @param m
     * @throws Exception
     */
    public void getAiDianyingAllmovieInit(String url , String num, int s , int m) throws Exception {

        StringBuffer stringBuffer = new StringBuffer(url);
        stringBuffer.append("/");

        stringBuffer.append(m);

        stringBuffer.append("/");
        stringBuffer.append(s);
        stringBuffer.append("/");
//        url = stringBuffer.toString();
////            int sleept=(int) (Math.random()*(5-1+1)+1);
////            Thread.sleep(sleept);
////            http://www.lxxh7.com/随机/随机/93687LjLXH.html#comments
//            System.out.println(url + num + "LjLXH.html");
//            MovieNameAndUrlModel movieNameAndUrlModel = aiDianyingService.getMovieLoopsAiDianying(url + num +"LjLXH.html");
//            List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
//            if (StrUtil.isNotBlank(movieNameAndUrlModel.getMovieName())& StrUtil.isNotBlank(movieNameAndUrlModel.getWangPanPassword())) {
//                movieNameAndUrlModelList.add(movieNameAndUrlModel);
//                iMovieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList,"url_movie_aidianying");
//                log.info("插入成功 -》 " + movieNameAndUrlModel.getMovieUrl());
//            }
    }


    /**
     * 未读影单初始化数据导入
     * @param url
     * @throws Exception
     */
    public void getUnreadAllmovieInit(String url) throws Exception {

        StringBuffer stringBuffer = new StringBuffer(url);
//
//        int sleept=(int) (Math.random()*(5000-1000+1000)+1000);
//        Thread.sleep(sleept);

        MovieNameAndUrlModel movieNameAndUrlModel = unReadService.getUnReadMovieLoops(url,"123",123);
        List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
        if (StrUtil.isNotBlank(movieNameAndUrlModel.getMovieName())& StrUtil.isNotBlank(movieNameAndUrlModel.getWangPanPassword())) {
            movieNameAndUrlModelList.add(movieNameAndUrlModel);
            iMovieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList,"url_movie_unread");
            log.info("插入成功 -》 " + movieNameAndUrlModel.getMovieUrl());
        }
    }




}

