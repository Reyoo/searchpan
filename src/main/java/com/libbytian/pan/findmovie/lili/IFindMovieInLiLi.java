package com.libbytian.pan.findmovie.lili;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 项目名: pan
 * 文件名: IFindMovieInLiLi
 * 创建者: HS
 * 创建时间:2022/1/13 17:04
 * 描述: TODO
 */
@CacheConfig(cacheNames = "lili")
public interface IFindMovieInLiLi extends IService<MovieNameAndUrlModel> {

    @Cacheable(key = "#movieName", condition = "#movieName != null")
    List<MovieNameAndUrlModel> findMovieUrl(String movieName) throws Exception;

}
