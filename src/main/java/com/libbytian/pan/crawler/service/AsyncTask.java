package com.libbytian.pan.crawler.service;

import cn.hutool.core.util.StrUtil;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import com.libbytian.pan.wechat.service.NormalPageService;
import com.libbytian.pan.wechat.service.aidianying.AiDianyingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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


    private final NormalPageService normalPageService;
    private final AiDianyingService aiDianyingService;

    private final IMovieNameAndUrlService iMovieNameAndUrlService;


//    @Async("taskExecutor")
    public void getAllmovieInit(String url) throws Exception {
        Random random = new Random();

        int s = random.nextInt(29) % (29 - 10 + 1) + 10;
        int m = random.nextInt(12) % (12-11 + 1) +11;


        StringBuffer stringBuffer = new StringBuffer(url);
        stringBuffer.append("/");

        stringBuffer.append(m);

        stringBuffer.append("/");
        stringBuffer.append(s);
        stringBuffer.append("/");
        url = stringBuffer.toString();

        for (int i = 7440; i <= 10000; i++) {

            int num=(int) (Math.random()*(5-1+1)+1);
            System.out.println(num);
            Thread.sleep(num);
//            http://www.lxxh7.com/随机/随机/93687LjLXH.html#comments
            System.out.println(url + i + "LjLXH.html");
            MovieNameAndUrlModel movieNameAndUrlModel = aiDianyingService.getMovieLoopsAiDianying(url + i +"LjLXH.html");
            ArrayList arrayList = new ArrayList();
            if (StrUtil.isNotBlank(movieNameAndUrlModel.getMovieName())& StrUtil.isNotBlank(movieNameAndUrlModel.getWangPanPassword())) {
                arrayList.add(movieNameAndUrlModel);
                iMovieNameAndUrlService.addOrUpdateMovieUrls(arrayList,"url_movie_aidianying");
            }

        }
    }


    /**
     * 爱电影
     * @param url
     * @param num
     * @param s
     * @param m
     * @throws Exception
     */
    public void getAllmovieInit(String url ,String num,int s ,int m) throws Exception {


        StringBuffer stringBuffer = new StringBuffer(url);
        stringBuffer.append("/");

        stringBuffer.append(m);

        stringBuffer.append("/");
        stringBuffer.append(s);
        stringBuffer.append("/");
        url = stringBuffer.toString();
            int sleept=(int) (Math.random()*(5-1+1)+1);
            Thread.sleep(sleept);
//            http://www.lxxh7.com/随机/随机/93687LjLXH.html#comments
            System.out.println(url + num + "LjLXH.html");
            MovieNameAndUrlModel movieNameAndUrlModel = aiDianyingService.getMovieLoopsAiDianying(url + num +"LjLXH.html");
            List<MovieNameAndUrlModel> movieNameAndUrlModelList = new ArrayList<>();
            if (StrUtil.isNotBlank(movieNameAndUrlModel.getMovieName())& StrUtil.isNotBlank(movieNameAndUrlModel.getWangPanPassword())) {
                movieNameAndUrlModelList.add(movieNameAndUrlModel);
                iMovieNameAndUrlService.addOrUpdateMovieUrls(movieNameAndUrlModelList,"url_movie_aidianying");
                log.info("插入成功 -》 " + movieNameAndUrlModel.getMovieUrl());
            }

    }




}

