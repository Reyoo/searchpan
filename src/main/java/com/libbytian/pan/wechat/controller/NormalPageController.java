package com.libbytian.pan.wechat.controller;


import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import com.libbytian.pan.wechat.service.NormalPageService;
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


    @Value("${user.unread.weiduyingdan}")
    String unreadUrl;
    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;


    @GetMapping("/lxxh/search")
//    @RequestLimit(count = 3, frameTime = 2, lockTime = 30)
    public AjaxResult getLxxhdMovie(@RequestParam String name){

        try {
            MovieNameAndUrlModel movieNameAndUrl;
            movieNameAndUrl = normalPageService.getMovieLoopsAiDianying(lxxhUrl+"/?s="+name);
            List<MovieNameAndUrlModel> arrayList = new ArrayList();
            arrayList.add(movieNameAndUrl);
            movieNameAndUrlService.addOrUpdateMovieUrls(arrayList,"url_movie_aidianying");
            return AjaxResult.success(movieNameAndUrl);
        } catch (Exception e){
            return AjaxResult.error(e.getMessage());
        }

    }



    @GetMapping("search")
//    @RequestLimit(count = 3, frameTime = 2, lockTime = 30)
    public AjaxResult getAllMovie(@RequestParam String name){
        List<MovieNameAndUrlModel> realMovieList = new ArrayList();
        List<MovieNameAndUrlModel> movieNameAndUrls =normalPageService.getNormalUrl(unreadUrl+"/?s="+name);

        movieNameAndUrls.stream().forEach( movieNameAndUrl ->
                realMovieList.add(normalPageService.getMoviePanUrl(movieNameAndUrl)));
        List<MovieNameAndUrlModel> movieNameAndUrls1 =normalPageService.getNormalUrl(lxxhUrl+"/?s="+name);
        movieNameAndUrls1.stream().forEach( movieNameAndUrl ->
                realMovieList.add(normalPageService.getMoviePanUrl2(movieNameAndUrl)));

        return AjaxResult.success(realMovieList);
    }
}
