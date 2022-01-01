package com.libbytian.pan.findmovie.sumsu;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.MovieNameAndUrlMapper;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author SunQi
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FindMovieInSumsuImpl extends ServiceImpl<MovieNameAndUrlMapper, MovieNameAndUrlModel> implements IFindMovieInSumsu {

    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Override
    public List<MovieNameAndUrlModel> findMovieUrl(String movieName) throws Exception {

        return  movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_sumsu", movieName);

    }
}
