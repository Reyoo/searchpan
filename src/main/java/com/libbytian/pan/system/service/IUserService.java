package com.libbytian.pan.system.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemUserModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface IUserService extends IService<SystemUserModel> {



    int deleteUserByUsername(String username);

    SystemUserModel updateUser(SystemUserModel user) throws Exception;

    IPage<SystemUserModel> findByPage(Page<SystemUserModel> page);

    IPage<SystemUserModel> findConditionByPage(Page<SystemUserModel> page, SystemUserModel user);



}