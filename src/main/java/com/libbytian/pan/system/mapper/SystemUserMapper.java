package com.libbytian.pan.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    @Select("SELECT user_id as userId,user_name as username,user_mobile as mobile,user_password as password,user_last_login_time AS lastLoginTime,createTime AS createTime,status AS status FROM sys_user WHERE user_name = #{0}")
    SystemUserModel selectUserByUsername(String username);


    @Select("SELECT COUNT(*) FROM sys_user WHERE user_name = #{username} ")
    int selectByName(String username);

    @Select("SELECT t.template_id AS templateid , template_name AS templatename , template_createtime AS templatecreatetime , template_lastupdate AS templatelastupdate , template_status AS templatestatus FROM sys_template t LEFT JOIN user_template ut ON t.template_id = ut.template_id LEFT JOIN sys_user u ON ut.user_id = u.user_id WHERE u.user_name = #{username}")
    List<SystemTemplateModel> findTemplateById(String username);


    @Select("SELECT act_time AS acttime FROM sys_user WHERE user_name = #{username}")
    LocalDateTime findActTime(String username);


}
