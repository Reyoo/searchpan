package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemRoleToPermission;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToRole;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



public interface ISystemUserToRoleService extends IService<SystemUserToRole> {

    List<SystemUserToRole> getUserToRoleObject(SystemUserToRole systemUserToRole) throws Exception;

    int removeUserToRoleObject(SystemUserToRole systemUserToRole);

    int addUserToRoleModel(SystemUserToRole systemUserToRole);

    int dropUserRoleByUserModel(SystemUserModel systemUserModel);


}
