package com.libbytian.pan.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemRoleMapper;
import com.libbytian.pan.system.model.SystemRoleModel;
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
    public List<SystemRoleModel> getRolenameByUserId(String username)  throws Exception{
        return systemRoleMapper.selectUserByUserId(username);
    }


    @Override
    public SystemRoleModel getRoleByRolename(String rolename)  throws Exception{
        return systemRoleMapper.selectRoleByRolename(rolename);
    }

    @Override
    public List<SystemRoleModel> getRoleByUserName(String username)  throws Exception{
        return systemRoleMapper.selectRoleByUsername(username);
    }

}

