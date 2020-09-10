package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemRoleModel;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface ISystemRoleService extends IService<SystemRoleModel> {

    List<SystemRoleModel> getRolenameByUserId(String userId);


    SystemRoleModel getRoleByRolename(String rolename);

}