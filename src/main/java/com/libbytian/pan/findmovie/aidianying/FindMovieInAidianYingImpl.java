package com.libbytian.pan.findmovie.aidianying;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.MovieNameAndUrlMapper;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;




@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FindMovieInAidianYingImpl  extends ServiceImpl<MovieNameAndUrlMapper, MovieNameAndUrlModel> implements IFindMovieInAiDianYing {

    private final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Override
    public List<MovieNameAndUrlModel> findMovieUrl(String movieName) throws Exception {

        return  movieNameAndUrlMapper.selectMovieUrlByLikeName("url_movie_aidianying", movieName);

    }
}
