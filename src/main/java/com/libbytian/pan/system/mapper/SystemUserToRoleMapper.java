package com.libbytian.pan.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemRoleToPermission;
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


    List<SystemUserToRole> selectUserToRoleObject(SystemUserToRole systemUserToRole);



    int deleteUserToRoleObject(SystemUserToRole systemUserToRole);

    int insertUserToRoleModel(SystemUserToRole systemUserToRole);


    int deleteUserRoleByUserModel(SystemUserModel systemUserModel);
}
