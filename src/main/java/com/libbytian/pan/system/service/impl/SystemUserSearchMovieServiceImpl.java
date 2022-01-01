package com.libbytian.pan.system.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemUserSearchMovieMapper;
import com.libbytian.pan.system.model.SystemUserSearchMovieModel;
import com.libbytian.pan.system.service.ISystemUserSearchMovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemUserSearchMovieServiceImpl extends ServiceImpl<SystemUserSearchMovieMapper, SystemUserSearchMovieModel> implements ISystemUserSearchMovieService {

    private  final SystemUserSearchMovieMapper systemUserSearchMovieMapper;



    @Override
    @Async("crawler-Executor")
    public void userSearchMovieCountInFindfish(String searchStr){
        if(StrUtil.isEmpty(searchStr)){
            return;
        }
        //先去查询是否有插入此词条的记录
        SystemUserSearchMovieModel systemUserSearchMovieModel = getUserSearchMovieBySearchName(searchStr);
        //如果查询回来的结果为空 则插入
        if(systemUserSearchMovieModel == null){
            SystemUserSearchMovieModel initSystemUserSearchMovieModel = new SystemUserSearchMovieModel(searchStr,1, LocalDateTime.now(),Boolean.TRUE);
            if(addUserSearchMovie(initSystemUserSearchMovieModel)){
                log.info("--> " + searchStr  + "  新增词条插入成功");
            }else {
                log.info("xxx--> " + searchStr  + "  新增词条插入失败");
            }
        }else {
            int searcchTimes = systemUserSearchMovieModel.searchTimes() + 1;
            systemUserSearchMovieModel.searchTimes(searcchTimes);
            systemUserSearchMovieModel.lastSearchTime(LocalDateTime.now());

            if( updateUserSearchMovie(systemUserSearchMovieModel)){
                log.info("--> " + searchStr  + "  更新词条插入成功 第" + searcchTimes + "次查询" );
            }else {
                log.info("xxx--> " + searchStr  + "  更新词条插入失败");
            }
        }

    }





    /**
     * 新增 用户搜索记录
     * @param systemUserSearchMovieModel
     * @return
     */
    @Override
    public Boolean addUserSearchMovie(SystemUserSearchMovieModel systemUserSearchMovieModel) {
        int insertSucccess = 0;
        insertSucccess = systemUserSearchMovieMapper.insertUserSearchMovie(systemUserSearchMovieModel);
        if(insertSucccess>0){
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
    }

    /**
     * 查询用户搜索记录
     * @param searchName
     * @return
     */
    @Override
    public SystemUserSearchMovieModel getUserSearchMovieBySearchName(String searchName) {
        return systemUserSearchMovieMapper.getUserSearchMovieBySearchName(searchName);
    }

    /**
     * 更新用户搜索记录
     * @param systemUserSearchMovieModel
     * @return
     */
    @Override
    public Boolean updateUserSearchMovie(SystemUserSearchMovieModel systemUserSearchMovieModel) {
        int insertSucccess = 0;
        insertSucccess = systemUserSearchMovieMapper.updateUserSearchMovie(systemUserSearchMovieModel);
        if(insertSucccess>0){
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
    }

    /**
     * 根据时间范围 获取用户查询电影名
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public List<SystemUserSearchMovieModel> listUserSearchMovieBySearchDateRange(String beginTime, String endTime) {
        return systemUserSearchMovieMapper.listUserSearchMovieBySearchDateRange(beginTime,endTime);
    }
}
