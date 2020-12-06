package com.libbytian.pan.wechat.controller;


import com.libbytian.pan.system.aop.RequestLimit;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.wechat.service.NormalPageService;
import lombok.RequiredArgsConstructor;
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
public class NormalPageController {

    private final NormalPageService normalPageService;


    @Value("${user.unread.weiduyingdan}")
    String unreadUrl;
    @Value("${user.lxxh.aidianying}")
    String lxxhUrl;

    @GetMapping("/unread/search")
    @RequestLimit(count = 3, frameTime = 2, lockTime = 30)
    public AjaxResult getUnreadMovie(@RequestParam String name){
        List<MovieNameAndUrlModel> movieNameAndUrls =(List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(unreadUrl+"/?s="+name).get("data");
        List<MovieNameAndUrlModel> realMovieList = new ArrayList();
        movieNameAndUrls.stream().forEach( movieNameAndUrl ->
                realMovieList.add(normalPageService.getMoviePanUrl(movieNameAndUrl)));
        return AjaxResult.success(realMovieList);
    }

    @GetMapping("/lxxh/search")
    @RequestLimit(count = 3, frameTime = 2, lockTime = 30)
    public AjaxResult getLxxhdMovie(@RequestParam String name){

        List<MovieNameAndUrlModel> movieNameAndUrls =(List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(lxxhUrl+"/?s="+name).get("data");
        List<MovieNameAndUrlModel> realMovieList = new ArrayList();

        movieNameAndUrls.stream().forEach(movieNameAndUrl ->
                realMovieList.add(normalPageService.getMoviePanUrl2(movieNameAndUrl)));


        return AjaxResult.success(realMovieList);
    }



    @GetMapping("search")
    @RequestLimit(count = 3, frameTime = 2, lockTime = 30)    
    public AjaxResult getAllMovie(@RequestParam String name){
        List<MovieNameAndUrlModel> realMovieList = new ArrayList();
        List<MovieNameAndUrlModel> movieNameAndUrls =(List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(unreadUrl+"/?s="+name).get("data");

        movieNameAndUrls.stream().forEach( movieNameAndUrl ->
                realMovieList.add(normalPageService.getMoviePanUrl(movieNameAndUrl)));
        List<MovieNameAndUrlModel> movieNameAndUrls1 =(List<MovieNameAndUrlModel>) normalPageService.getNormalUrl(lxxhUrl+"/?s="+name).get("data");
        movieNameAndUrls1.stream().forEach( movieNameAndUrl ->
                realMovieList.add(normalPageService.getMoviePanUrl2(movieNameAndUrl)));

        return AjaxResult.success(realMovieList);
    }
}
