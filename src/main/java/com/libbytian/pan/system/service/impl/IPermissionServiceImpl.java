package com.libbytian.pan.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    public IPage<SystemPermissionModel> findPermission(Page page, SystemPermissionModel systemPermissionModel) {


        QueryWrapper queryWrapper = new QueryWrapper();

        /**
         * 这里systemusermodel 不做空判断 。getusername 空指针  null.getUsername
         */
        if(systemPermissionModel != null){
            if(systemPermissionModel.getPermissionUrl() != null){
                queryWrapper.eq("permission_url",systemPermissionModel.getPermissionUrl());
            }

            if(systemPermissionModel.getPermissionComment() != null){
                queryWrapper.eq("permission_comment",systemPermissionModel.getPermissionComment());
            }
            if(systemPermissionModel.getPermissionstatus() != null){
                queryWrapper.eq("permission_status",systemPermissionModel.getPermissionstatus());
            }
        }
        queryWrapper.orderByDesc("createtime");

        return permissionMapper.selectPage(page,queryWrapper);


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
