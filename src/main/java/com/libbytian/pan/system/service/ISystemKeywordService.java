package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemKeywordModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 项目名: pan
 * 文件名: IKeywordService
 * 创建者: HS
 * 创建时间:2020/12/14 15:50
 * 描述: TODO
 */

@Transactional(propagation = Propagation.REQUIRES_NEW)
//@CacheConfig(cacheNames = "useKeyword")
public interface ISystemKeywordService extends IService<SystemKeywordModel> {

//    @Cacheable(key = "#username",condition = "#username != null")
    SystemKeywordModel getKeywordByUser(String username);

//    @CachePut(key = "#systemKeywordModel.user" ,condition = "#systemKeywordModel != null")
    void updateKeyword(SystemKeywordModel systemKeywordModel);


    int addkeyword(SystemKeywordModel systemKeywordModel);


    int dropKeywordByUser(SystemUserModel systemUserModel);
}
