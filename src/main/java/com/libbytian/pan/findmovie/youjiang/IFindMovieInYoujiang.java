package com.libbytian.pan.findmovie.youjiang;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 项目名: pan
 * 文件名: IFindMovieInXiaoyou
 * 创建者: HS
 * 创建时间:2021/2/28 2:00
 * 描述: TODO
 */
@CacheConfig(cacheNames = "youjiang")
public interface IFindMovieInYoujiang extends IService<MovieNameAndUrlModel> {

    @Cacheable(key = "#movieName", condition = "#movieName != null")
    List<MovieNameAndUrlModel> findMovieUrl(String movieName) throws Exception;
}
