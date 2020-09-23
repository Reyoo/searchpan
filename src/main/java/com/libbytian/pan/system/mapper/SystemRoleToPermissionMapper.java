package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemRoleToPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SystemRoleToPermissionMapper extends BaseMapper<SystemRoleToPermission> {

    @Select("select role_permission_id AS rolepermissionid ,role_id AS roleid,permission_id AS permissionid, role_permission_status AS rolepermissionstatus from role_permission where role_id = #{0}")
    List<SystemRoleToPermission> selectPermissionByRoleid(String roleId);
}
