package com.libbytian.pan.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *
 */
@Mapper
public interface SystemUserToRoleMapper extends BaseMapper<SystemUserToRole> {

    @Select("select id AS userToRoleId ,user_id,role_id, user_role_status from sys_user_role where user_id = #{0}")
    List<SystemUserToRole> selectUserByUserid(String userId);


}
