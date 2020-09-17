package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface IRoleService extends IService<SystemRoleModel> {

    IPage<SystemUserModel> findUserByRole(Page<SystemRoleModel> page , String roleName);
    IPage<SystemRoleModel> findRoleById(Page<SystemRoleModel> page , String roleId);
    int roleNameCount(String roleName);
    int addRole(SystemRoleModel role);
    int putRole(SystemRoleModel role);
    int dropRole(String roleId);


    boolean checkEmail(String roleName);
}
