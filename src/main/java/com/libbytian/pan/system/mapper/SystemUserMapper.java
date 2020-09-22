package com.libbytian.pan.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.libbytian.pan.system.model.SystemUserModel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

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
@Repository
public interface SystemUserMapper extends BaseMapper<SystemUserModel> {

    @Select("select user_id as userId,user_name as username,user_mobile as mobile,user_password as password,user_last_login_time AS lastLoginTime,createTime AS createTime,status AS status from sys_user where user_name = #{0}")
    SystemUserModel selectUserByUsername(String username);


    @Select("select user_id as userId,user_name as username,user_mobile as mobile,user_password as password,user_last_login_time AS lastLoginTime,createTime AS createTime,status AS status from sys_user")
    List<SystemUserModel> selectAll();


    @Delete("delete from sys_user where user_name = #{0}")
    int deleteUserByUsername(String username);


    @Select("select COUNT(*) from sys_user WHERE user_name = #{username} ")
    int selectByName(String username);

}
