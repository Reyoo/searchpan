package com.libbytian.pan.findmovie.aidianying;


import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@CacheConfig(cacheNames = "aidianying")
public interface IFindMovieInAiDianYing extends IService<MovieNameAndUrlModel> {

    @Cacheable(key = "#movieName", condition = "#movieName != null")
    List<MovieNameAndUrlModel> findMovieUrl(String movieName) throws Exception;

}
