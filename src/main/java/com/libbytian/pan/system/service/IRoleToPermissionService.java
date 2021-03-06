package com.libbytian.pan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.libbytian.pan.system.model.SystemRoleToPermission;
import com.libbytian.pan.system.model.SystemUserToRole;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface IRoleToPermissionService extends IService<SystemRoleToPermission> {

     int removeRoleToPermission(SystemRoleToPermission systemRoleToPermission);

     List<SystemRoleToPermission> listRoleToPermissionObjects(SystemRoleToPermission systemRoleToPermission);

}
