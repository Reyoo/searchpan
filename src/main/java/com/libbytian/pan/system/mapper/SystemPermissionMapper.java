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
     * 根据模板id查询模板
     * @param permissionIds
     * @return
     */
    List<SystemPermissionModel> selectPermissionByPermissionId(List<String> permissionIds);




    int insertPermission(SystemPermissionModel systemPermissionModel);


    int updateFindfishPermission(SystemPermissionModel systemPermissionModel);

    int deleteFindfishPermission(SystemPermissionModel systemPermissionModel);

}
