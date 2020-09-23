package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemRoleToPermissionMapper;
import com.libbytian.pan.system.model.SystemRoleToPermission;
import com.libbytian.pan.system.service.IRoleToPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IRoleToPermissionServiceImpl extends ServiceImpl<SystemRoleToPermissionMapper, SystemRoleToPermission> implements IRoleToPermissionService {

    private final SystemRoleToPermissionMapper systemRoleToPermissionMapper;

    @Override
    public List<SystemRoleToPermission> getRolePermissionByroleID(String roleId) throws Exception {
        return systemRoleToPermissionMapper.selectPermissionByRoleid(roleId);
    }
}
