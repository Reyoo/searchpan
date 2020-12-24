package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;

import java.util.List;
import java.util.Map;

/**
 * @author: QiSun
 * @date: 2020-12-06
 * @Description:
 */
public interface IMovieNameAndUrlService extends IService<MovieNameAndUrlModel> {

    List<MovieNameAndUrlModel> findMovieUrl (String tablename,String movieName ) throws  Exception;


    /**
     * 获取爱电影
     * @param movieName
     * @return
     * @throws Exception
     */


    List<MovieNameAndUrlModel> findAiDianYingUrl(String movieName) throws  Exception;

    void addOrUpdateAiDianYingMovieUrls(List<MovieNameAndUrlModel> movieNameAndUrlModels,String tablename) throws Exception;

    /**
     * 获取未读影单
     * @param movieName
     * @return
     * @throws Exception
     */
    List<MovieNameAndUrlModel> findUnReadMovieUrl(String movieName) throws  Exception;

    void addOrUpdateMovieUrls(List<MovieNameAndUrlModel> movieNameAndUrlModels,String tablename)  throws Exception;

    int addMovieUrl(MovieNameAndUrlModel movieNameAndUrlModels) throws Exception;

    int dropMovieUrl (MovieNameAndUrlModel movieNameAndUrlModel) throws Exception;
}
