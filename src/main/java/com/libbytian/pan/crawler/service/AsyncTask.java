package com.libbytian.pan.crawler.service;

import cn.hutool.core.util.StrUtil;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import com.libbytian.pan.system.service.impl.MovieNameAndUrlServiceImpl;
import com.libbytian.pan.wechat.service.NormalPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private final IMovieNameAndUrlService iMovieNameAndUrlService;


    //    @Async("taskExecutor")
    public void getAllmovieInit(String url) throws Exception {


        for (int i = 50001; i <= 60000; i++) {


            int num=(int) (Math.random()*(5-1+1)+1);
            System.out.println(num);
            Thread.sleep(num);
            MovieNameAndUrlModel movieNameAndUrlModel = normalPageService.getMovieLoops(url + i);
            ArrayList arrayList = new ArrayList();
            if (StrUtil.isNotBlank(movieNameAndUrlModel.getMovieName())& StrUtil.isNotBlank(movieNameAndUrlModel.getWangPanPassword())) {
                arrayList.add(movieNameAndUrlModel);

                iMovieNameAndUrlService.addMovieUrl(arrayList);
            }

        }
    }

}

