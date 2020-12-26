package com.libbytian.pan.system.service.impl;

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

        for (MovieNameAndUrlModel movieNameAndUrlModel : movieNameAndUrlModels) {

            List<MovieNameAndUrlModel> list = movieNameAndUrlMapper.selectMovieUrlByName(tableName, movieNameAndUrlModel.getMovieName().trim());
            if (list.size() > 0) {
//                如果查询到数据 则更新
                movieNameAndUrlMapper.updateUrlMovieUrl(tableName,movieNameAndUrlModel);
                log.info("更新电影列表-->" + movieNameAndUrlModel);

            } else {
                movieNameAndUrlMapper.insertMovieUrl(tableName,movieNameAndUrlModel);
                log.info("插入电影列表-->" + movieNameAndUrlModel);
            }
        }

    }

    @Override
    public int dropMovieUrl(String tableName ,MovieNameAndUrlModel movieNameAndUrlModel) throws Exception {
        return movieNameAndUrlMapper.deleteUrlMovieUrls(tableName  ,movieNameAndUrlModel);
    }



}
