package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISystemTemDetailsService extends IService<SystemTemDetailsModel> {

    int addTemDetails(SystemTemDetailsModel systemTemDetailsModel,String templateId) throws Exception;

    int exportExceltoDb(String filename, InputStream inputStream, String templateId) throws Exception;


     void exportTemDetails(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, @RequestParam String templateId)  throws Exception;


}
