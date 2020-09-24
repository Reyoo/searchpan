package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISystemTemDetailsService extends IService<SystemTemDetailsModel> {


    int addTemDetails(SystemTemDetailsModel systemTemDetailsModel,String templateId) throws Exception;

    int updateTemDetails(SystemTemDetailsModel systemTemDetailsModel) throws Exception;


    int exportExceltoDb(String filename, InputStream inputStream, String templateId) throws Exception;

}
