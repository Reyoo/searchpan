package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToTemplate;


public interface ISystemUserToTemplateService extends IService<SystemUserToTemplate> {

    int removieTemplateIdAll(String tempid);

    int dropUserToTemplateByUserId(SystemUserModel systemUserModel) throws Exception;






}
