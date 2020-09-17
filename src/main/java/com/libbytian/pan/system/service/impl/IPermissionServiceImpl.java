package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.PermissionMapper;
import com.libbytian.pan.system.model.SystemPermissionModel;
import com.libbytian.pan.system.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class IPermissionServiceImpl extends ServiceImpl<PermissionMapper, SystemPermissionModel> implements IPermissionService {

    private final PermissionMapper permissionMapper;

    @Override
    public IPage<SystemPermissionModel> findPermission(Page page, String username) {

        return permissionMapper.selectByPage(page,username);


    }

    @Override
    public int putPermission(String name, SystemPermissionModel body) {

       String permissionUrl = body.getPermissionUrl();
       String permissionComment = body.getPermissionComment();
       String permissionId = body.getPermissionId();


        return permissionMapper.update(permissionUrl,permissionComment,permissionId);
    }

    @Override
    public int dropPermission(String permissionId) {

        return permissionMapper.deleteById(permissionId);
    }

    @Override
    public int addPermission(SystemPermissionModel permission) {

        LocalDateTime localDateTime = LocalDateTime.now();

        String permissionComment = permission.getPermissionComment();
        String permissionUrl = permission.getPermissionUrl();

        return permissionMapper.add(permissionUrl,permissionComment,localDateTime);
    }
}