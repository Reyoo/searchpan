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
import java.util.UUID;

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
        QueryWrapper<SystemPermissionModel> queryWrapper = new QueryWrapper();
        /**
         * 这里systemusermodel 不做空判断 。getusername 空指针  null.getUsername
         */
        if(systemPermissionModel != null){
            if(StrUtil.isNotEmpty(systemPermissionModel.permissionUrl())){
                queryWrapper.lambda().eq(SystemPermissionModel::permissionUrl,systemPermissionModel.permissionUrl());
            }
            if(StrUtil.isNotEmpty(systemPermissionModel.permissionComment())){
                queryWrapper.lambda().eq(SystemPermissionModel::permissionComment,systemPermissionModel.permissionComment());
            }
            if (systemPermissionModel.createtime() != null){
                queryWrapper.lambda().eq(SystemPermissionModel::createtime,systemPermissionModel.createtime());
            }
            if(systemPermissionModel.permissionstatus() != null){
                queryWrapper.lambda().eq(SystemPermissionModel::permissionstatus,systemPermissionModel.permissionstatus());
            }
        }
        queryWrapper.lambda().orderByDesc(SystemPermissionModel::createtime);
        return systemPermissionMapper.selectPage(page,queryWrapper);

    }


    @Override
    public int savePermission(SystemPermissionModel permission) throws Exception {

        permission.createtime(LocalDateTime.now());
        permission.permissionId(UUID.randomUUID().toString());
        if(StrUtil.isBlank(permission.permissionComment())){
            throw new Exception("权限名不允许为空");
        }
        return systemPermissionMapper.insertPermission(permission);
    }


    /**
     * 更新权限
     * @param systemPermissionModel
     * @return
     */
    @Override
    public int patchFindfishPermission(SystemPermissionModel systemPermissionModel) {
        return systemPermissionMapper.updateFindfishPermission(systemPermissionModel);
    }

    /**
     * 删除权限
     * @param systemPermissionModel
     * @return
     */
    @Override
    public int removeFindfishPermission(SystemPermissionModel systemPermissionModel) {
        return systemPermissionMapper.deleteFindfishPermission(systemPermissionModel);
    }

    @Override
    public List<SystemPermissionModel> listPermissionByPermission(List<String> permissionIds) {
        return systemPermissionMapper.selectPermissionByPermissionId(permissionIds);
    }
}

