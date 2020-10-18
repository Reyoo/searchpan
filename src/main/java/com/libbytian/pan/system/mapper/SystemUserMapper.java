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


    SystemUserModel getUser(SystemUserModel systemUserModel);

    List<SystemUserModel> listUsers(SystemUserModel systemUserModel);

    /**
     * QiSun
     */
    SystemUserModel selectOneUserModel(SystemUserModel systemUserModel);

    @Delete("DELETE u,ur,ut FROM sys_user u LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id LEFT JOIN user_template ut ON u.user_id = ut.user_id WHERE u.user_id = '60'")
    void removeUserAll(SystemUserModel user);


}
