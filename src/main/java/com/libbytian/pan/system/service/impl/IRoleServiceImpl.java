package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemRoleMapper;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IRoleServiceImpl extends ServiceImpl<SystemRoleMapper, SystemRoleModel> implements IRoleService {

    private final SystemRoleMapper roleMapper;


    @Override
    public IPage<SystemUserModel> findUserByRole(Page<SystemRoleModel> page, String roleName) {

        IPage<SystemUserModel> result = roleMapper.selectUserByRole(page,roleName);

        return result;

    }

    @Override
    public IPage<SystemUserModel> findRoleById(Page<SystemRoleModel> page, String roleId) {

       IPage<SystemRoleModel> result = roleMapper.selectRoleById(page,roleId);

        return null;
    }

    @Override
    public int roleNameCount(String roleName) {

        return roleMapper.roleNameCount(roleName);
    }

    @Override
    public int addRole(SystemRoleModel role) {

        String roleId = role.getRoleId();
        String rolename =  role.getRoleName();
        LocalDateTime localDateTime = LocalDateTime.now();
        return roleMapper.insert(roleId,rolename,localDateTime);
    }

    @Override
    public int putRole(SystemRoleModel role) {

        String roleId = role.getRoleId();
        String rolename =  role.getRoleName();
        return roleMapper.update(roleId,rolename);
    }

    @Override
    public int dropRole(String roleId) {

        return roleMapper.delete(roleId);
    }
}
