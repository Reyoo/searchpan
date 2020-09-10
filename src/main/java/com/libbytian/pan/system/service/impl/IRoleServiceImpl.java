package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.RoleMapper;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IRoleServiceImpl extends ServiceImpl<RoleMapper, SystemRoleModel> implements IRoleService {

    private final RoleMapper roleMapper;


    @Override
    public IPage<SystemRoleModel> select(Page<SystemRoleModel> page, SystemRoleModel role) {
        return null;
    }
}
