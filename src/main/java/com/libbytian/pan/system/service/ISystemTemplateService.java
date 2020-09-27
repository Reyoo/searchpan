package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemplateModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISystemTemplateService extends IService<SystemTemplateModel> {


     IPage<SystemTemDetailsModel> findTemDetailsPage(Page page,String templateId) throws Exception;

     List<SystemTemDetailsModel> findTemDetails(String templateId) throws Exception;

     IPage<SystemTemplateModel> findTemById(Page page,SystemTemplateModel systemTemDetailsModel) throws Exception;


}
