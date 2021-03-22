package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;

import java.util.List;


public interface ISystemTemplateService extends IService<SystemTemplateModel> {


     /**
      * userId redis 的key
      * @param systemUserModel
      * @return
      * @throws Exception
      */
     List<SystemTemplateModel> listTemplatelByUser(SystemUserModel systemUserModel) throws Exception;


     List<SystemTemplateModel> listTemplatelObjects(SystemTemplateModel systemTemplateModel) throws Exception;

     boolean checkTemplateIsBinded(SystemUserModel systemUserModel);


     /**
      * 根据用户删除模板表
      * @param systemUserModel
      * @return
      * @throws Exception
      */
     int dropTemplateByUser(SystemUserModel systemUserModel) throws  Exception;

     /**
      * 根据模板详情ID获取模板名称
      * @param temdetailsId
      * @return
      */
     public SystemTemplateModel getTemplateById(String temdetailsId);


     /**
      * 查询所有模板
      * @return
      */
     List<SystemTemplateModel> getAllTemplate();

}
