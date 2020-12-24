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
     * 获取爱电影
     * @param movieName
     * @return
     * @throws Exception
     */
    @Override
    public List<MovieNameAndUrlModel> findAiDianYingUrl(String movieName) throws Exception {
        return movieNameAndUrlMapper.selectAiDianYingMovieUrlByLikeName(movieName);
    }



    /**
     * 插入更新操作、如果数据库中 不存在 则插入、如果存在 则更新  由于分表、每个爬虫资源影单单独一套 Controller Servcie Mapper
     *
     * @param movieNameAndUrlModels
     * @return
     * @throws Exception
     */
    @Override
    public void addOrUpdateAiDianYingMovieUrls(List<MovieNameAndUrlModel> movieNameAndUrlModels,String tablename) throws Exception {

        for (MovieNameAndUrlModel movieNameAndUrlModel : movieNameAndUrlModels) {

            List<MovieNameAndUrlModel> list = movieNameAndUrlMapper.selectAiDianYingMovieUrlByName(movieNameAndUrlModel.getMovieName().trim());
            if (list.size() > 0) {
//                如果查询到数据 则更新
                movieNameAndUrlMapper.updateAiDianYingUrlMovieUrls(movieNameAndUrlModel);
                log.info("更新电影列表-->" + movieNameAndUrlModel);

            } else {
                movieNameAndUrlMapper.insertMovieUrls(movieNameAndUrlModels);
                log.info("插入电影列表-->" + movieNameAndUrlModel);
            }
        }

    }




    /**
     * 获取未读影单
     * @param movieName
     * @return
     * @throws Exception
     */
    @Override
    public List<MovieNameAndUrlModel> findUnReadMovieUrl(String movieName) throws Exception {
        return movieNameAndUrlMapper.selectUnReadMovieUrlByLikeName(movieName);
    }


    /**
     * 插入更新操作、如果数据库中 不存在 则插入、如果存在 则更新  由于分表、每个爬虫资源影单单独一套 Controller Servcie Mapper
     *
     * @param movieNameAndUrlModels
     * @return
     * @throws Exception
     */
    @Override
    public void addOrUpdateMovieUrls(List<MovieNameAndUrlModel> movieNameAndUrlModels,String tablename) throws Exception {

        for (MovieNameAndUrlModel movieNameAndUrlModel : movieNameAndUrlModels) {

            List<MovieNameAndUrlModel> list = movieNameAndUrlMapper.selectMovieUrlByName(tablename, movieNameAndUrlModel.getMovieName().trim());
            if (list.size() > 0) {
//                如果查询到数据 则更新
                movieNameAndUrlMapper.updateUrlMovieUrls(movieNameAndUrlModel);
                log.info("更新电影列表-->" + movieNameAndUrlModel);

            } else {
                movieNameAndUrlMapper.insertMovieUrls(movieNameAndUrlModels);
                log.info("插入电影列表-->" + movieNameAndUrlModel);
            }
        }

    }

    @Override
    public int dropMovieUrl(MovieNameAndUrlModel movieNameAndUrlModel) throws Exception {
        return movieNameAndUrlMapper.deleteUrlMovieUrls(movieNameAndUrlModel);
    }

    @Override
    public int addMovieUrl(MovieNameAndUrlModel movieNameAndUrlModels) throws Exception {
        return movieNameAndUrlMapper.insertMovieUrl(movieNameAndUrlModels);
    }

}
