package com.libbytian.pan.system.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.libbytian.pan.system.mapper.SystemPermissionMapper;
import com.libbytian.pan.system.model.SystemPermissionModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description：  权限业务实现类
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SystemPermissionImpl extends ServiceImpl<SystemPermissionMapper, SystemPermissionModel> implements ISystemPermissionService {

    private final SystemPermissionMapper systemPermissionMapper ;

    @Override
    public List<SystemPermissionModel> listPermissionByUser(SystemUserModel systemUserModel) throws Exception {

        return systemPermissionMapper.listPermissionByUser(systemUserModel);
    }


    @Override
    public IPage<SystemPermissionModel> findPermission(Page page, SystemPermissionModel systemPermissionModel) throws Exception {


        QueryWrapper queryWrapper = new QueryWrapper();

        /**
         * 这里systemusermodel 不做空判断 。getusername 空指针  null.getUsername
         */
        if(systemPermissionModel != null){
            if(systemPermissionModel.getPermissionUrl() != null&&!systemPermissionModel.getPermissionUrl().equals("")){
                queryWrapper.eq("permission_url",systemPermissionModel.getPermissionUrl());
            }

            if(systemPermissionModel.getPermissionComment() != null&&!"".equals(systemPermissionModel.getPermissionComment())){
                queryWrapper.eq("permission_comment",systemPermissionModel.getPermissionComment());
            }
            if (systemPermissionModel.getCreatetime() != null){
                queryWrapper.eq("createtime",systemPermissionModel.getCreatetime());
            }


            if(systemPermissionModel.getPermissionstatus() != null){
                queryWrapper.eq("permission_status",systemPermissionModel.getPermissionstatus());

            }



        }
        queryWrapper.orderByDesc("createtime");

        return systemPermissionMapper.selectPage(page,queryWrapper);


    }


    @Override
    public int savePermission(SystemPermissionModel permission) throws Exception {

        permission.setCreatetime(LocalDateTime.now());

        if(StrUtil.isBlank(permission.getPermissionComment())){
            throw new Exception("权限名不允许为空");
        }

        return systemPermissionMapper.insertPermission(permission);
    }
}

