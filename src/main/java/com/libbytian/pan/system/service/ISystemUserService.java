package com.libbytian.pan.system.service;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToTemplate;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liugh123
 * @since 2018-05-03
 */
@Transactional(propagation = Propagation.REQUIRES_NEW)
//@CacheConfig(cacheNames = "userModel")
public interface ISystemUserService extends IService<SystemUserModel> {



    /**
     * 查询 返回单个用户信息
     * @param user
     * @return
     */
//    @Cacheable(key = "#user",condition = "#user != null")
    SystemUserModel getUser(SystemUserModel user) ;

    /**
     * 查询 返回多个用户信息
     * @param systemUserModel
     * @return
     */
    List<SystemUserModel> listUsers(SystemUserModel systemUserModel) ;

    /**
     * 注册用户
     * @param user
     * @return
     */
    SystemUserModel register(SystemUserModel user ) throws Exception;



    /**
     * 删除用户时，同时删除权限关联表，模板关联表中数据
     * @param user
     */
    void removeUserAll(SystemUserModel user) throws Exception;


//    @CachePut(key = "#user" ,condition = "#user != null")
    int updateUser(SystemUserModel user) throws Exception;

    IPage<SystemUserModel> findConditionByPage(Page<SystemUserModel> page, SystemUserModel user) throws Exception;


    /**
     * 用户校验
     * @param user
     * @return
     * @throws Exception
     */
    Boolean checkUserStatus(SystemUserModel user) throws Exception;


    /**
     * 根据模板ID查询用户
     * @param templateId
     * @return
     */
    SystemUserModel getUserByUerToTemplate(String templateId);



    Boolean checkUserCouldDel(SystemUserModel user) throws Exception;


    int addSystemUser(SystemUserModel systemUserModel ) throws Exception;



}
