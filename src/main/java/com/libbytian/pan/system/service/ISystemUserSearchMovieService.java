package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemUserSearchMovieModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: QiSun
 * @date: 2021-01-29
 * @Description: 统计一段时间用户搜索电影名词记录
 */

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISystemUserSearchMovieService extends IService<SystemUserSearchMovieModel> {




     void userSearchMovieCountInFindfish(String searchStr);

    /**
     * 新增 用户搜索记录
     * @param systemUserSearchMovieModel
     * @return
     */
    Boolean addUserSearchMovie(SystemUserSearchMovieModel systemUserSearchMovieModel);

    /**
     * 查询用户搜索记录
     * @param searchName
     * @return
     */
    SystemUserSearchMovieModel getUserSearchMovieBySearchName(String searchName);

    /**
     * 更新用户搜索记录
     * @param systemUserSearchMovieModel
     * @return
     */
    Boolean updateUserSearchMovie(SystemUserSearchMovieModel systemUserSearchMovieModel);

    /**
     * 根据时间范围 获取用户查询电影名
     * @param beginTime
     * @param endTime
     * @return
     */
    List<SystemUserSearchMovieModel> listUserSearchMovieBySearchDateRange(String beginTime,String endTime);


    /**
     * 根据时间范围查询 搜索频率最高的10条记录
      */

}
