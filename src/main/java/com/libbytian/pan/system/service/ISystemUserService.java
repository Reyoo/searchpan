package com.libbytian.pan.system.service;



import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author liugh123
 * @since 2018-05-03
 */
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISystemUserService extends IService<SystemUserModel> {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户
     */
    SystemUserModel getUserByUserName(String username) ;


    /**
     * 注册用户
     * @param user
     * @return
     */
    SystemUserModel register(SystemUserModel user ) throws Exception;

    /**
     * 查询用户是否存在
     * @param username
     * @return
     */
    int selectByName(String username) throws Exception;


    SystemUserModel updateUser(SystemUserModel user) throws Exception;

    IPage<SystemUserModel> findConditionByPage(Page<SystemUserModel> page, SystemUserModel user) throws Exception;


    List<SystemTemplateModel> findTemplateById(String username);

    SystemUserModel findByUsername(String username) throws Exception;

}
