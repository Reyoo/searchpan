package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * @author: QiSun
 * @date: 2020-12-06
 * @Description:
 */
@Mapper
public interface MovieNameAndUrlMapper extends BaseMapper<MovieNameAndUrlModel> {


    List<MovieNameAndUrlModel> selectMovieUrlByName(String tablename , String movieName);



    List<MovieNameAndUrlModel> selectAiDianYingMovieUrlByName(String movieName);

    /**
     * 查找爱电影电影名资源
     * @param movieName
     * @return
     */
    List<MovieNameAndUrlModel> selectAiDianYingMovieUrlByLikeName(String movieName);


    /**
     * 未读爱电影批量新增
     * @param movieNameAndUrlModels
     * @return
     */
    int insertMovieUrls(List<MovieNameAndUrlModel> movieNameAndUrlModels);

    /**
     * 爱电影新增
     * @param movieNameAndUrlModel
     * @return
     */
    int insertMovieUrl(MovieNameAndUrlModel movieNameAndUrlModel);


    /**
     * 删除爱电影资源
     * @param movieNameAndUrlModel
     * @return
     */
    int deleteUrlMovieUrls(MovieNameAndUrlModel movieNameAndUrlModel);


    /**
     * 更新爱电影资源
     * @param movieNameAndUrlModel
     * @return
     */




    int updateAiDianYingUrlMovieUrls(MovieNameAndUrlModel movieNameAndUrlModel);





    /**
     * 查找未读影单电影资源
     * @param movieName
     * @return
     */
    List<MovieNameAndUrlModel> selectUnReadMovieUrlByLikeName(String movieName);


    /**
     * 暂时未使用
     * @param movieNameAndUrlModel
     * @return
     */
    int updateUrlMovieUrls(MovieNameAndUrlModel movieNameAndUrlModel);

}
