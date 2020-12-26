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


    /**
     * 动态传入table
     * @param tablename
     * @param movieName
     * @return
     * @throws Exception
     */
    List<MovieNameAndUrlModel> findMovieUrl (String tablename,String movieName ) throws  Exception;


    void addOrUpdateMovieUrls(List<MovieNameAndUrlModel> movieNameAndUrlModels,String tableName)  throws Exception;


    int dropMovieUrl (String tableName,MovieNameAndUrlModel movieNameAndUrlModel) throws Exception;
}
