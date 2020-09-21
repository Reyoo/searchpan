package com.libbytian.pan.system.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemPermissionModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface IPermissionService extends IService<SystemPermissionModel> {

    IPage<SystemPermissionModel> findPermission(Page page, SystemPermissionModel systemPermissionModel) throws Exception;

    int putPermission(String name, SystemPermissionModel body) throws Exception;
    int dropPermission(String permissionId) throws Exception;

    int addPermission(SystemPermissionModel permission) throws Exception;

}

