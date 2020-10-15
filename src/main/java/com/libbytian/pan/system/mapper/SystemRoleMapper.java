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


    @Select("SELECT u.user_id AS userId, u.user_name AS username ,r.show_name AS ,showName ,u.user_password AS password ,u.createtime AS createTime ,u.status AS status  FROM sys_user u LEFT JOIN sys_user_role sr on u.user_id = sr.user_id LEFT JOIN sys_role r on sr.role_id = r.role_id WHERE r.role_name = #{roleName}")
    IPage<SystemUserModel> selectUserByRole(Page page , String roleName);

    @Select("SELECT r.role_name AS roleName , r.show_name AS ,showName , r.createtime,r.role_status AS roleStatus FROM sys_role WHERE r.role_id = #{roleId}")
    IPage<SystemRoleModel> selectRoleById(Page page , String roleId);

//    @Insert("INSERT INTO sys_role (role_id,role_name ,show_name ,createtime,role_status) VALUES(#{roleId},#{roleName},#{showName},#{localDateTime},0)")
//    int insert(String roleId, String roleName, String showName,LocalDateTime localDateTime);


//    @Update("<script>"
//            + "UPDATE `sys_role` "
//            + "<set>"
//            + "<if test='roleName != null'>role_name = #{roleName}, </if>"
//            + "</set>"
//            + "WHERE role_id = #{roleId} ;"
//            + "</script>")

    @Update("UPDATE sys_role role_name = #{roleName} WHERE  role_id = #{roleId}")
    int update(String roleId,String roleName);


    @Select("SELECT COUNT(*) FROM sys_role WHERE role_name = #{roleName}")
    int roleNameCount(String roleName);


    @Select("SELECT r.role_id  AS roleId, r.role_name AS roleName,r.role_status AS roleStatus,r.createtime AS createtime\n" +
            "FROM sys_role r LEFT JOIN sys_user_role u  ON r.role_id = u.role_id \n" +
            "LEFT JOIN sys_user s ON u.user_id = s.user_id\n" +
            "WHERE s.user_name = #{username}")
    List<SystemRoleModel> selectRoleByUsername(String username);

    @Select("select role_id AS roleId , role_name AS roleName, role_status AS roleStatus from sys_role where role_id in (select role_id from sys_user_role where user_id = #{0})")
    List<SystemRoleModel> selectUserByUserId(String userId);


    @Select("select role_id AS roleId , role_name AS roleName, role_status AS roleStatus from sys_role where role_name = #{0}")
    SystemRoleModel selectRoleByRolename(String roleName);


    @Select("SELECT r.role_id AS roleId FROM sys_role r LEFT JOIN sys_user_role ur ON r.role_id = ur.role_id LEFT JOIN " +
            "sys_user u ON ur.user_id = u.user_id WHERE u.user_name =#{username}")
    String[] getRoleIdByUsername(String username);

}
