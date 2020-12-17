package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.MovieNameAndUrlMapper;
import com.libbytian.pan.system.mapper.SystemRoleToPermissionMapper;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import com.libbytian.pan.system.model.SystemRoleToPermission;
import com.libbytian.pan.system.service.IMovieNameAndUrlService;
import com.libbytian.pan.system.service.IRoleToPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: QiSun
 * @date: 2020-12-06
 * @Description:
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MovieNameAndUrlServiceImpl extends ServiceImpl<MovieNameAndUrlMapper, MovieNameAndUrlModel> implements IMovieNameAndUrlService {


    private  final MovieNameAndUrlMapper movieNameAndUrlMapper;

    @Override
    public List<MovieNameAndUrlModel> findMovieUrl(String movieName) throws Exception {
        return  movieNameAndUrlMapper.selectMovieUrlByName(movieName);
    }

    @Override
    public List<MovieNameAndUrlModel> findLikeMovieUrl(String movieName) throws Exception {
        return  movieNameAndUrlMapper.selectMovieUrlByLikeName(movieName);
    }

    @Override
    public int addMovieUrl(List<MovieNameAndUrlModel> movieNameAndUrlModels) throws Exception{
        return movieNameAndUrlMapper.insertMovieUrls(movieNameAndUrlModels);
    }

    @Override
    public int dropMovieUrl(MovieNameAndUrlModel movieNameAndUrlModel) throws Exception {
        return movieNameAndUrlMapper.deleteUrlMovieUrls(movieNameAndUrlModel);
    }
}
