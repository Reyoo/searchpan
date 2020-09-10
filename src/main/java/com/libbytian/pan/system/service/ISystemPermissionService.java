package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemPermissionModel;
import com.libbytian.pan.system.model.SystemRoleModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISystemPermissionService extends IService<SystemPermissionModel> {

    /**
     * 根据用户名获取所有的权限url
     * @param username
     * @return
     */
    List<SystemPermissionModel> getPermissionByUsername(String username);

}
