package com.libbytian.pan.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemRoleMapper;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemRoleService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemRoleImpl extends ServiceImpl<SystemRoleMapper, SystemRoleModel> implements ISystemRoleService {

    private final SystemRoleMapper systemRoleMapper ;

    @Override
    public List<SystemRoleModel> getRolenameByUserId(String username)   {
        return systemRoleMapper.selectUserByUserId(username);
    }


    @Override
    public SystemRoleModel getRoleByRolename(String rolename)   {
        return systemRoleMapper.selectRoleByRolename(rolename);
    }

    @Override
    public List<SystemRoleModel> getRoleByUserName(String username)  throws Exception{
        return systemRoleMapper.selectRoleByUsername(username);
    }

    @Override
    public String[] getRoleIdByUsername(String username) {
        return systemRoleMapper.getRoleIdByUsername(username);
    }


    /**
     * 根据用户信息获取用户角色信息
     * @param systemUserModel
     * @return
     */
    @Override
    public List<SystemRoleModel> getRoleInfoByUser(SystemUserModel systemUserModel) {
        return systemRoleMapper.selectUserRoleByUser(systemUserModel);
    }
}

