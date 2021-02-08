package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemTemToTemdetail;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@CacheConfig(cacheNames = "templateDetails")
public interface ISystemTmplToTmplDetailsService extends IService<SystemTemToTemdetail> {


    int addTemplateToTemplateDetail(List<SystemTemToTemdetail> systemTemToTemdetails);


    /**
     *  删除用户指定模板详细
     * @param templateIds
     * @return
     */


    int dropTemplateAndDetails(List<String> templateIds );


}
