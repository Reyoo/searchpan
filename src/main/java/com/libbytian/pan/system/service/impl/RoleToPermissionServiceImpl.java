package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemRoleToPermissionMapper;
import com.libbytian.pan.system.model.SystemRoleToPermission;
import com.libbytian.pan.system.service.IRoleToPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author SunQi
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleToPermissionServiceImpl extends ServiceImpl<SystemRoleToPermissionMapper, SystemRoleToPermission> implements IRoleToPermissionService {

    private final SystemRoleToPermissionMapper systemRoleToPermissionMapper;


    @Override
    public int removeRoleToPermission(SystemRoleToPermission systemRoleToPermission) {

      return systemRoleToPermissionMapper.deleteRoleToPermission(systemRoleToPermission);
    }

    @Override
    public List<SystemRoleToPermission> listRoleToPermissionObjects(SystemRoleToPermission systemRoleToPermission) {
        return systemRoleToPermissionMapper.listRoleToPermissionObjects(systemRoleToPermission);
    }
}
