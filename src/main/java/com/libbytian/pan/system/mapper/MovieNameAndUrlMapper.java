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


    List<MovieNameAndUrlModel> selectMovieUrlByName (String movieName)   ;

    List<MovieNameAndUrlModel> selectMovieUrlByLikeName (String movieName)   ;


     int insertMovieUrls(List<MovieNameAndUrlModel> movieNameAndUrlModels);


     int insertMovieUrl(MovieNameAndUrlModel movieNameAndUrlModel);

     int deleteUrlMovieUrls(MovieNameAndUrlModel movieNameAndUrlModel);
}
