package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@CacheConfig(cacheNames = "templateDetails")
public interface ISystemTemDetailsService extends IService<SystemTemDetailsModel> {


    IPage<SystemTemDetailsModel> findTemDetailsPage(Page page,String templateId) throws Exception;


    @CachePut(key = "#username" ,condition = "#username != null")
    List<SystemTemDetailsModel> addTemDetails(SystemTemDetailsModel systemTemDetailsModel,String templateId,String username) throws Exception;

    @CachePut(key = "#username" ,condition = "#username != null")
    List<SystemTemDetailsModel> exportExceltoDb(String filename, InputStream inputStream, String templateId,String username) throws Exception;


     void exportTemDetails(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,  List<String> temdetailsIds)  throws Exception;


    /**
     * 根据用户信息获取 启用状态的模板详细List
     */
    @Cacheable(key = "#systemUserModel.username",condition = "#systemUserModel.username != null")
    List<SystemTemDetailsModel> getTemDetailsWithUser (SystemUserModel systemUserModel) throws Exception;


    /**
     * 更新模板
     * @param SystemTemDetailsModel
     * @return
     */
    @CachePut(key = "#username" ,condition = "#username != null")
    List<SystemTemDetailsModel> updateTempDetailsWithModel(SystemTemDetailsModel SystemTemDetailsModel,String username) throws Exception;



    List<SystemTemDetailsModel> listTemDetailsObjectsByWord(SystemTemDetailsModel systemTemDetailsModel);

    void  defaultSave(String templateId);







    @CacheEvict(key = "#username" ,condition = "#username != null" ,allEntries = true,beforeInvocation = true )
    int deleteTemplateDetails(List<String> temdetailsId,String username);

}
