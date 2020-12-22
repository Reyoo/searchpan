package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemRoleToPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SystemRoleToPermissionMapper extends BaseMapper<SystemRoleToPermission> {

    int deleteRoleToPermission(SystemRoleToPermission systemRoleToPermission);

    List<SystemRoleToPermission> listRoleToPermissionObjects(SystemRoleToPermission systemRoleToPermission);

    /**
     * 根据角色信息删除 角色权限捆绑表
     * @param
     * @return
     */
    int deleteRoleToPermissionByRole(SystemRoleModel systemRoleModel);


}
