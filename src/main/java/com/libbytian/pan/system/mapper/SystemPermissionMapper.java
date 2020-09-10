package com.libbytian.pan.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.libbytian.pan.system.model.SystemPermissionModel;
import com.libbytian.pan.system.model.SystemRoleModel;
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
public interface SystemPermissionMapper extends BaseMapper<SystemPermissionModel> {

    @Select("SELECT \n" +
            "p.permission_id AS permissionId,\n" +
            "permission_url AS permissionUrl,\n" +
            "permission_comment AS permissionComment,\n" +
            "permission_status AS permissionstatus \n" +
            "FROM\n" +
            "sys_permission p LEFT JOIN role_permission r\n" +
            "ON  p.permission_id = r.permission_id\n" +
            "LEFT JOIN  sys_user_role  s \n" +
            "ON r.role_id = s.role_id\n" +
            "LEFT JOIN sys_user u \n" +
            "ON u.user_id = s.user_id\n" +
            "where u.user_name \n= #{0}")
    List<SystemPermissionModel> selectUserByUsername(String username);

}
