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


    List<MovieNameAndUrlModel> selectMovieUrlByLikeName(String tablename , String movieName);

    /**
     * 批量新增
     * @param movieNameAndUrlModels
     * @return
     */
    int insertMovieUrls(String tableName,List<MovieNameAndUrlModel> movieNameAndUrlModels);

    /**
     * 新增
     * @param movieNameAndUrlModel
     * @return
     */
    int insertMovieUrl(String tableName ,MovieNameAndUrlModel movieNameAndUrlModel);


    /**
     * 删除资源
     * @param movieNameAndUrlModel
     * @return
     */
    int deleteUrlMovieUrls(String tableName ,MovieNameAndUrlModel movieNameAndUrlModel);





    /**
     * 更新电影资源
     * @param movieNameAndUrlModel
     * @return
     */
    int updateUrlMovieUrl(String tableName, MovieNameAndUrlModel movieNameAndUrlModel);

}
