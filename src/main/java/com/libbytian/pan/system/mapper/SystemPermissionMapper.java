package com.libbytian.pan.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.model.SystemPermissionModel;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
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


    List<SystemPermissionModel> listPermissionByUser(SystemUserModel systemUserModel);


    /**
     * 分页
     * 传用户 颗粒度可以更细 暂时不做这么细的权限划分
     * @param page
     * @param username
     * @return
     */
    @Select("SELECT P.permission_id AS permissionId,permission_url As permissionUrl,permission_comment AS permissionComment,permission_status As permissionstatus ,createtime from \n" +
            "sys_permission p LEFT JOIN role_permission r ON  p.permission_id = r.permission_id LEFT JOIN sys_user_role s ON r.role_id = s.role_id LEFT JOIN sys_user u ON s.user_id = u.user_id WHERE u.user_name = #{username}")
    IPage<SystemPermissionModel> selectByPage(Page page, String username);



    int insertPermission(SystemPermissionModel systemPermissionModel);

}
