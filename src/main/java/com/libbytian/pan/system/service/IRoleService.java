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

    IPage<SystemUserModel> findUserByRole(Page<SystemRoleModel> page , String roleName) throws Exception;
    IPage<SystemRoleModel> findRoleById(Page<SystemRoleModel> page , String roleId) throws Exception;
    IPage<SystemRoleModel> findRole(Page<SystemRoleModel> page , SystemRoleModel systemRoleModel) throws Exception;
    int roleNameCount(String roleName);
    int addRole(SystemRoleModel role) throws Exception;
    int putRole(SystemRoleModel role);
    int dropRole(String roleId) throws Exception;


    boolean checkEmail(String roleName) throws Exception;
}
