package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(propagation = Propagation.REQUIRES_NEW)
//@CacheConfig(cacheNames = "systemRoleModel")
public interface ISystemRoleService extends IService<SystemRoleModel> {



    /**
     * 通过user查询role
     * @param user
     * @return
     */
//    @Cacheable(key = "#user.username",condition = "#user != null")
    List<SystemRoleModel> listRolesByUser(SystemUserModel user);

    SystemRoleModel getRoles(SystemRoleModel systemRoleModel);

    IPage<SystemRoleModel> getRolesPage(Page page , SystemRoleModel systemRoleModel);

    IPage<SystemRoleModel> findRoleById(Page<SystemRoleModel> page , String roleId) throws Exception;

    IPage<SystemRoleModel> findRole(Page<SystemRoleModel> page , SystemRoleModel systemRoleModel) throws Exception;

    int dropRole(SystemRoleModel systemRoleModel) throws Exception;

    boolean checkEmail(String roleName) throws Exception;

    List<SystemRoleModel> getRoleInfoByUser(SystemUserModel systemUserModel);

    Boolean checkRolerCouldDel(SystemRoleModel systemRoleModel) throws Exception;


    /**
     * 新增角色 单个角色额
     */

    int addFindFishRole(SystemRoleModel systemRoleModel);



}
