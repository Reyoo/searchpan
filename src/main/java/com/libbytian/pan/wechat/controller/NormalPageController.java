package com.libbytian.pan.wechat.controller;


import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import com.libbytian.pan.crawler.service.sumsu.CrawlerSumsuService;
import com.libbytian.pan.wechat.service.NormalPageService;
import com.libbytian.pan.crawler.service.aidianying.AiDianyingService;
import com.libbytian.pan.crawler.service.unread.UnReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sun7127
 * @description: 获取到所有第一方请求搜索页面
 */
@RestController
@RequestMapping("/home")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class NormalPageController {

    private final NormalPageService normalPageService;
    private final IMovieNameAndUrlService movieNameAndUrlService;
    private final CrawlerSumsuService crawlerSumsuService;
    private final AiDianyingService aiDianyingService;
    private final UnReadService unReadService;

    @Value("${user.unread.weiduyingdan}")
    String unreadUrl;
    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;

    @Value("${user.sumsu.url}")
    String sumSuUrl;

    @Value(("${user.lili.url}"))
    String liliUrl;


    /**
     * 接口有问题
     * @param name
     * @return
     */
//    @GetMapping("/lxxh/search")
////    @RequestLimit(count = 3, frameTime = 2, lockTime = 30)
//    public AjaxResult getLxxhdMovie(@RequestParam String name){
//
//        try {
//
//          List<MovieNameAndUrlModel> arrayList =aiDianyingService.getAiDianYingCrawlerResult(name);
//          movieNameAndUrlService.addOrUpdateMovieUrls(arrayList,"url_movie_aidianying");
//            return AjaxResult.success(arrayList);
//        } catch (Exception e){
//            return AjaxResult.error(e.getMessage());
//        }
//
//    }



    @GetMapping("search")
//    @RequestLimit(count = 3, frameTime = 2, lockTime = 30)
    public AjaxResult getAllMovie(@RequestParam String name){
        List<MovieNameAndUrlModel> movieNameAndUrls =unReadService.getUnReadCrawlerResult(name);

        return AjaxResult.success(movieNameAndUrls);
    }



    @GetMapping("sumsu")
//    @RequestLimit(count = 3, frameTime = 2, lockTime = 30)
    public AjaxResult getSumsuMovie(@RequestParam String searchMovieName) throws Exception{
        List<MovieNameAndUrlModel> realMovieList = new ArrayList();
        List<MovieNameAndUrlModel> movieNameAndUrls =crawlerSumsuService.getSumsuUrl(searchMovieName);
        return AjaxResult.success(movieNameAndUrls);
    }

    /**
     * 未完成
     * @param name
     * @return
     */
    @GetMapping("lili")
    public AjaxResult getLiliMovie(@RequestParam String name) {
        try {
            MovieNameAndUrlModel movieNameAndUrl;
            movieNameAndUrl = normalPageService.getMovieLoopsLiLi(liliUrl + "/?s=" + name);

            List<MovieNameAndUrlModel> arrayList = new ArrayList();
            arrayList.add(movieNameAndUrl);
            movieNameAndUrlService.addOrUpdateMovieUrls(arrayList, "url_movie_aidianying");
            return AjaxResult.success(movieNameAndUrl);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
