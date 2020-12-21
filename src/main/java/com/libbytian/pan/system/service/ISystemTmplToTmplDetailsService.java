package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemTemToTemdetail;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISystemTmplToTmplDetailsService extends IService<SystemTemToTemdetail> {


    int addTemplateToTemplateDetail(List<SystemTemToTemdetail> systemTemToTemdetails);


    /**
     * 根据模板详细删除 模板与模板详细关联表中模板详细的绑定关联
     * @param templateIds
     * @return
     */
    int dropTemplateAndDetails(List<String> templateIds);


}
