package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SystemRoleMapper extends BaseMapper<SystemRoleModel> {



    @Select("SELECT r.role_name AS roleName , r.show_name AS ,showName , r.createtime,r.role_status AS roleStatus FROM sys_role WHERE r.role_id = #{roleId}")
    IPage<SystemRoleModel> selectRoleById(Page page , String roleId);


    IPage<SystemRoleModel> getRolesPage(Page page , SystemRoleModel systemRoleModel);


    SystemRoleModel getRoles(SystemRoleModel systemRoleModel);


    List<SystemRoleModel> listRolesByUser(SystemUserModel systemUserModel);



    List<SystemRoleModel> selectUserRoleByUser(SystemUserModel systemUserModel);

}
