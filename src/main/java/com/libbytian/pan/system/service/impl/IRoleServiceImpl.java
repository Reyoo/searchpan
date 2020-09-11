package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.RoleMapper;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IRoleServiceImpl extends ServiceImpl<RoleMapper, SystemRoleModel> implements IRoleService {

    private final RoleMapper roleMapper;


    @Override
    public IPage<SystemUserModel> findRole(Page<SystemRoleModel> page, String roleName) {

        IPage<SystemUserModel> result = roleMapper.select(page,roleName);

        return result;

    }

    @Override
    public int addRole(SystemRoleModel role) {

        String roleId = role.getRoleId();
        String rolename =  role.getRoleName();
        return roleMapper.insert(roleId,rolename);
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
