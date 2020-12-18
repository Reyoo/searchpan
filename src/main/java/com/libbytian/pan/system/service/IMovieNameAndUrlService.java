package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;

import java.util.List;

/**
 * @author: QiSun
 * @date: 2020-12-06
 * @Description:
 */
public interface IMovieNameAndUrlService extends IService<MovieNameAndUrlModel> {

    List<MovieNameAndUrlModel> findMovieUrl (String movieName) throws  Exception;
    List<MovieNameAndUrlModel> findLikeMovieUrl (String movieName) throws  Exception;

    int addMovieUrls(List<MovieNameAndUrlModel> movieNameAndUrlModels) throws Exception;

    int addMovieUrl(MovieNameAndUrlModel movieNameAndUrlModels) throws Exception;

    int dropMovieUrl (MovieNameAndUrlModel movieNameAndUrlModel) throws Exception;
}
