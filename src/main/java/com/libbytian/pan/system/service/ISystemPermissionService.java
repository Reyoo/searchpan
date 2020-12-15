package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemPermissionModel;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISystemPermissionService extends IService<SystemPermissionModel> {

    /**
     * 根据用户名获取所有的权限url
     * @param systemUserModel
     * @return
     */

    List<SystemPermissionModel> listPermissionByUser(SystemUserModel systemUserModel) throws Exception;


    IPage<SystemPermissionModel> findPermission(Page page, SystemPermissionModel systemPermissionModel) throws Exception;


    int savePermission(SystemPermissionModel permission) throws Exception;


}
