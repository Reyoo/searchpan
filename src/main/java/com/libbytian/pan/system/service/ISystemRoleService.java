package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISystemRoleService extends IService<SystemRoleModel> {



    /**
     * 通过user查询role
     * @param user
     * @return
     */
    List<SystemRoleModel> listRolesByUser(SystemUserModel user);


    SystemRoleModel getRoles(SystemRoleModel systemRoleModel);

    IPage<SystemRoleModel> getRolesPage(Page page , SystemRoleModel systemRoleModel);


    IPage<SystemRoleModel> findRoleById(Page<SystemRoleModel> page , String roleId) throws Exception;
    IPage<SystemRoleModel> findRole(Page<SystemRoleModel> page , SystemRoleModel systemRoleModel) throws Exception;

    int dropRole(String roleId) throws Exception;


    boolean checkEmail(String roleName) throws Exception;


}
