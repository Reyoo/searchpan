package com.libbytian.pan.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserToRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liugh123
 * @since 2018-05-03
 */
@Mapper
public interface SystemRoleMapper extends BaseMapper<SystemRoleModel> {

    @Select("select role_id AS roleId , role_name AS roleName, role_status AS roleStatus from sys_role where role_id in (select role_id from sys_user_role where user_id = #{0})")
    List<SystemRoleModel> selectUserByUsername(String userId);


    @Select("select role_id AS roleId , role_name AS roleName, role_status AS roleStatus from sys_role where role_name = #{0}")
    SystemRoleModel selectRoleByRolename(String roleName);

}
