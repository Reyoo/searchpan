package com.libbytian.pan.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.MovieNameAndUrlMapper;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: QiSun
 * @date: 2020-12-06
 * @Description:
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MovieNameAndUrlServiceImpl extends ServiceImpl<MovieNameAndUrlMapper, MovieNameAndUrlModel> implements IMovieNameAndUrlService {


    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Override
    public List<MovieNameAndUrlModel> findMovieUrl(String tablename,String movieName) throws Exception {
        return movieNameAndUrlMapper.selectMovieUrlByName(tablename,movieName);
    }



    /**
     * 插入更新操作、如果数据库中 不存在 则插入、如果存在 则更新  由于分表、每个爬虫资源影单单独一套 Controller Servcie Mapper
     *
     * @param movieNameAndUrlModels
     * @return
     * @throws Exception
     */
    @Override
    public void addOrUpdateMovieUrls(List<MovieNameAndUrlModel> movieNameAndUrlModels,String tableName) throws Exception {


        /**待测试*/
        movieNameAndUrlModels.parallelStream().filter( movieNameAndUrlModel -> StrUtil.isNotEmpty(movieNameAndUrlModel.getMovieName())).forEach(t -> {
            List<MovieNameAndUrlModel> list = movieNameAndUrlMapper.selectMovieUrlByName(tableName, t.getMovieName().trim());
            if (list.size() > 0) {
//                如果查询到数据 则更新
                movieNameAndUrlMapper.updateUrlMovieUrl(tableName,t);
                log.info("更新电影列表-->" + t);

            } else {
                movieNameAndUrlMapper.insertMovieUrl(tableName,t);
                log.info("插入电影列表-->" + t);
            }
        });

//        for (MovieNameAndUrlModel movieNameAndUrlModel : movieNameAndUrlModels) {
//
//            if(movieNameAndUrlModel.movieName()==null){
//                continue;
//            }
//            List<MovieNameAndUrlModel> list = movieNameAndUrlMapper.selectMovieUrlByName(tableName, movieNameAndUrlModel.getMovieName().trim());
//            if (list.size() > 0) {
////                如果查询到数据 则更新
//                movieNameAndUrlMapper.updateUrlMovieUrl(tableName,movieNameAndUrlModel);
//                log.info("更新电影列表-->" + movieNameAndUrlModel);
//
//            } else {
//                movieNameAndUrlMapper.insertMovieUrl(tableName,movieNameAndUrlModel);
//                log.info("插入电影列表-->" + movieNameAndUrlModel);
//            }
//        }

    }

    @Override
    public int dropMovieUrl(String tableName ,MovieNameAndUrlModel movieNameAndUrlModel) throws Exception {
        return movieNameAndUrlMapper.deleteUrlMovieUrls(tableName  ,movieNameAndUrlModel);
    }



}
