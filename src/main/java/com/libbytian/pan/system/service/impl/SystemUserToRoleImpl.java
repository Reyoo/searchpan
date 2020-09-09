package com.libbytian.pan.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemUserToRoleMapper;
import com.libbytian.pan.system.model.SystemUserToRole;
import com.libbytian.pan.system.service.ISystemUserToRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemUserToRoleImpl extends ServiceImpl<SystemUserToRoleMapper, SystemUserToRole> implements ISystemUserToRoleService {


    private final SystemUserToRoleMapper userToRoleMapper ;

    @Override
    public List<SystemUserToRole> getUserRoleByuserID(String userId) {
        return null;
    }
}
