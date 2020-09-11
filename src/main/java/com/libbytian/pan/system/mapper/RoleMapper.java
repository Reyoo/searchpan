package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.apache.ibatis.annotations.*;

@Mapper
public interface RoleMapper extends BaseMapper<SystemRoleModel> {


    @Select("SELECT u.user_id AS userId, u.user_name AS username ,u.user_password AS password ,u.createtime AS createTime ,u.status AS status  FROM sys_user u LEFT JOIN sys_user_role sr on u.user_id = sr.user_id LEFT JOIN sys_role r on sr.role_id = r.role_id WHERE r.role_name = #{roleName}")
    IPage<SystemUserModel> select(Page page , String roleName);

    @Insert("INSERT INTO sys_role (role_id,role_name,role_status) VALUES(#{roleId},#{roleName},0)")
    int insert(String roleId,String roleName);

    @Update("UPDATE sys_role role_name = #{roleName} WHERE  role_id = #{roleId}")
    int update(String roleId,String roleName);

    @Delete("DELETE FROM sys_role WHERE role_id = #{roleId}")
    int delete(String roleId);

}
