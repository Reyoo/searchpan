package com.libbytian.pan.wechat.service;


import com.libbytian.pan.crawler.service.aidianying.AiDianyingService;
import com.libbytian.pan.proxy.service.GetProxyService;
import com.libbytian.pan.proxy.service.PhantomJsProxyCallService;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.util.UserAgentUtil;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: pan
 * @Package: com.search.pan.system.service
 * @ClassName: NormalPageService
 * @Author: sun71
 * @Description: 获取
 * @Date: 2020/8/30 16:19
 * @Version: 1.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class NormalPageService {
    private final RestTemplate restTemplate;

    private final GetProxyService getProxyService;
    private final PhantomJsProxyCallService phantomJsProxyCallService;


    /**
     * 莉莉
     *
     * @param url
     * @return
     */
    public MovieNameAndUrlModel getMovieLoopsXiaoYou(String url) {

        String ipAndPort = getProxyService.getProxyIpFromRemote();
        MovieNameAndUrlModel movieNameAndUrlModel = new MovieNameAndUrlModel();
        movieNameAndUrlModel.setMovieUrl(url);
        try {

            PhantomJSDriver phantomJSDriver = phantomJsProxyCallService.create(url, ipAndPort);
            Document document = Jsoup.parse(phantomJSDriver.getPageSource());

            String name = document.getElementsByTag("title").first().text();
            movieNameAndUrlModel.setMovieName(name);


            Elements attr = document.getElementsByTag("p");
            for (Element element : attr) {
                for (Element aTag : element.getElementsByTag("a")) {

                    String linkhref = aTag.attr("href");
                    if (linkhref.startsWith("pan.baidu.com")) {
                        log.info("这里已经拿到要爬取的url : " + linkhref);
                        movieNameAndUrlModel.setWangPanUrl(linkhref);
                    }

                }
                if (element.text().contains("密码")) {
                    movieNameAndUrlModel.setWangPanPassword(element.text().split("【")[0].split(" ")[1]);
                }
            }

            return movieNameAndUrlModel;
        } catch (Exception e) {
            getProxyService.removeUnableProxy(ipAndPort);
            log.error(e.getMessage());
            return movieNameAndUrlModel;
        }
    }
}
