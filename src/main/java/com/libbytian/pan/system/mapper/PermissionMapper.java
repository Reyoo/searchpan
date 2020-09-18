package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.model.SystemPermissionModel;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface PermissionMapper extends BaseMapper<SystemPermissionModel> {


    @Select("SELECT P.permission_id AS permissionId,permission_url As permissionUrl,permission_comment AS permissionComment,permission_status As permissionstatus ,createtime from \n" +
            "sys_permission p LEFT JOIN role_permission r ON  p.permission_id = r.permission_id LEFT JOIN sys_user_role s ON r.role_id = s.role_id LEFT JOIN sys_user u ON s.user_id = u.user_id WHERE u.user_name = #{username}")
    IPage<SystemPermissionModel> selectByPage(Page page, String username);



//    @Update("UPDATE sys_permission SET permission_url  = #{permissionUrl} ,permission_comment  = #{permissionComment} WHERE permission_id  = #{permissionId}")

    @Update("<script>"
            + "UPDATE `sys_permission` "
            + "<set>"
            + "<if test='permissionUrl != null'>permission_url = #{permissionUrl}, </if>"
            + "<if test='permissionComment != null'>permission_comment = #{permissionComment}, </if>"
            + "</set>"
            + "WHERE permission_id = #{permissionId} ;"
            + "</script>")
    int update(String permissionUrl,String permissionComment,String permissionId);

    @Delete("DELETE FROM sys_permission WHERE permission_id  = #{permissionId}")
    int deleteById(String permissionId);

    @Insert("INSERT INTO sys_permission (permission_url,permission_comment,permission_status,createtime) VALUES(#{permissionUrl},#{permissionComment},0,#{localDateTime})")
    int add(String permissionUrl, String permissionComment, LocalDateTime localDateTime);



}
