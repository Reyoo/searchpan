package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISystemTemplateService extends IService<SystemTemplateModel> {



     List<SystemTemplateModel> listTemplatelByUser(SystemUserModel systemUserModel) throws Exception;


     List<SystemTemplateModel> listTemplatelObjects(SystemTemplateModel systemTemplateModel) throws Exception;

     boolean checkTemplateIsBinded(SystemUserModel systemUserModel);


}
