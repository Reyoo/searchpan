package com.libbytian.pan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.model.SystemUserModel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserMapper extends BaseMapper<SystemUserModel> {


//    @Select("select * from sys_user")
//    SystemUserModel selectAll();

    @Select("select user_id as userId,user_name as username,user_mobile as mobile,user_password as password,user_last_login_time AS lastLoginTime,createTime AS createTime,status AS status from sys_user")
    List<SystemUserModel> selectAll();

    @Select("select user_id as userId,user_name as username,user_mobile as mobile,user_password as password,user_last_login_time AS lastLoginTime,createTime AS createTime,status AS status from sys_user where user_name = #{username}")
    SystemUserModel selectUserByUsername(String username);

    @Delete("delete from sys_user where user_name = #{0}")
    int deleteUserByUsername(String username);


    @Select("select COUNT(*) from sys_user WHERE user_name = #{username} ")
    int selectByName(String username);


//    @Select(" \"<script>\"\n" +
//            "            +\"select user_id as userId,user_name as username,user_mobile as mobile,user_password as password,user_last_login_time AS lastLoginTime,createtime AS createTime,status AS status from sys_user\"\n" +
//            "            + \"WHERE\n" +
//            "            + \"<if test='user_id!=null '>\"\n" +
//            "            + \"'user_id' = #{user.userId}\"\n" +
//            "            + \"</if>\"\n" +
//            "            + \"<if test='user_name!=null '>\"\n" +
//            "            + \"'user_name' = #{user.username}\"\n" +
//            "            + \"</if>\"\n" +
//            "            + \"<if test='user_mobile!=null '>\"\n" +
//            "            + \"'user_mobile' = #{user.mobile}\"\n" +
//            "            + \"</if>\"\n" +
//            "            + \"<if test='user_password!=null '>\"\n" +
//            "            + \"'user_password' = #{user.password}\"\n" +
//            "            + \"</if>\"\n" +
//            "            + \"<if test='user_last_login_time!=null '>\"\n" +
//            "            + \"'user_last_login_time' = #{user.lastLoginTime}\"\n" +
//            "            + \"</if>\"\n" +
//            "            + \"<if test='createtime!=null '>\"\n" +
//            "            + \"'createtime' = #{user.createTime}\"\n" +
//            "            + \"</if>\"\n" +
//            "            + \"<if test='status!=null '>\"\n" +
//            "            + \"'status' = #{user.status}\"\n" +
//            "            + \"</if>\"\n" +
//            "            + \"</script>\"")

//    @Select("<script>SELECT * FROM `sys_user` <if test='user_id != null'>where user_id = #{userId}</if><script>")


//    IPage<SystemUserModel> selectUser(Page page ,SystemUserModel user);




}
