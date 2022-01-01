package com.libbytian.pan.system.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.*;
import com.libbytian.pan.system.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/role")
@Slf4j
public class FindFishRoleController {

    private final IRoleToPermissionService iRoleToPermissionService;
    private final ISystemUserToRoleService iSystemUserToRoleService;
    private final ISystemPermissionService iPermissionService;
    private final ISystemRoleService iSystemRoleService;

    /**
     * 查询角色
     *
     * @param systemRoleModel
     * @return
     */
    @RequestMapping(value = "/findrole", method = RequestMethod.POST)
    public AjaxResult findUserByRole(@RequestBody(required = false) SystemRoleModel systemRoleModel) {

        long page = 1L;
        long limits = 10L;
        if (systemRoleModel != null) {
            if (systemRoleModel.page() != null) {
                page = Long.valueOf(systemRoleModel.page());
            }
            if (systemRoleModel.limits() != null) {
                limits = Long.valueOf(systemRoleModel.limits());
            }
        }
        Page<SystemRoleModel> syspage = new Page<>(page, limits);
        try {
            IPage<SystemRoleModel> result = iSystemRoleService.findRole(syspage, systemRoleModel);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }

    }

    /**
     * 新增角色
     *
     * @param role
     * @return
     */
    @RequestMapping(value = "/addrole", method = RequestMethod.POST)
    public AjaxResult addRole(@RequestBody SystemRoleModel role) {
        try {
            role.createTime(LocalDateTime.now());
            role.roleId(UUID.randomUUID().toString());
            iSystemRoleService.addFindFishRole(role);
            return AjaxResult.success();
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }

    }

    /**
     * 根据角色ID修改角色名
     *
     * @param role
     * @return
     */
    @RequestMapping(value = "/updaterole", method = RequestMethod.PATCH)
    public AjaxResult putRole(@RequestBody SystemRoleModel role) {

        try {
            if (iSystemRoleService.checkRolerCouldDel(role)) {
                iSystemRoleService.updateById(role);
                return AjaxResult.success();
            } else {
                return AjaxResult.error("该角色为系统保留用户禁止修改");
            }

        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * HuangS
     * 根据角色ID删除角色
     *
     * @param role 必传（roleId，roleName）
     * @return
     */
    @RequestMapping(value = "/droprole", method = RequestMethod.DELETE)
    public AjaxResult dropRole(@RequestBody SystemRoleModel role) {

        try {
            if (StringUtils.isBlank(role.roleId())) {
                return AjaxResult.error("字段不能为空");
            }

            if (!iSystemRoleService.checkRolerCouldDel(role)) {
                return AjaxResult.error("该角色为系统保留用户禁止删除");
            }

            //查询用户角色表,判断角色是否绑定用户
            SystemUserToRole systemUserToRole = new SystemUserToRole();
            systemUserToRole.roleId(role.roleId());
            int count = iSystemUserToRoleService.getUserToRoleObject(systemUserToRole).size();
            if (count > 0) {
                return AjaxResult.error("该角色已绑定用户，不可删除");
            }
            iSystemRoleService.dropRole(role);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 查询角色下绑定的权限
     *
     * @param systemRoleModel
     * @return
     */
    @RequestMapping(value = "/getauth", method = RequestMethod.POST)
    public AjaxResult finduserRole(@RequestBody SystemRoleModel systemRoleModel) {

        try {
            SystemRoleToPermission systemRoleToPermission = new SystemRoleToPermission();
            systemRoleToPermission.roleId(systemRoleModel.roleId());
            //获取到角色管理权限的id集合
            List<SystemRoleToPermission> systemRoleToPermissionList = iRoleToPermissionService.listRoleToPermissionObjects(systemRoleToPermission);
            if (CollectionUtil.isEmpty(systemRoleToPermissionList)) {
                List<SystemPermissionModel> systemRoleToPermissions = iPermissionService.list();
                return AjaxResult.success(systemRoleToPermissions);
            }

            List<String> permissionId = systemRoleToPermissionList.stream().map(SystemRoleToPermission::permissionId).collect(Collectors.toList());
            List<SystemPermissionModel> systemRoleModelListAll = iPermissionService.listPermissionByPermission(permissionId);
            systemRoleModelListAll.forEach(permissionModel -> permissionModel.checked(true));
            List<SystemPermissionModel> systemPermissionModelList = iPermissionService.list();
            //id为两个列表相同属性，取出A的list中的id
            List<String> permissionIdList = systemRoleModelListAll.stream().map(SystemPermissionModel::permissionId).collect(Collectors.toList());
            //B列表去除A列表已有的数据
            systemPermissionModelList = systemPermissionModelList.stream().filter(SystemRoleToPermission -> !permissionIdList.contains(SystemRoleToPermission.permissionId())).collect(Collectors.toList());
            systemPermissionModelList.forEach(permissionModel -> permissionModel.checked(false));
            systemPermissionModelList.addAll(systemRoleModelListAll);
            return AjaxResult.success(systemPermissionModelList);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 更新角色权限表
     *
     * @param systemRoleToPermission
     * @return
     */
    @RequestMapping(value = "/updateauth", method = RequestMethod.PATCH)
    public AjaxResult updateuserRole(@RequestBody SystemRoleToPermission systemRoleToPermission) {

        try {
            if (systemRoleToPermission != null) {
                if (systemRoleToPermission.checked()) {
                    //更新
                    return AjaxResult.success(iRoleToPermissionService.save(systemRoleToPermission));
                } else {
                    //删除
                    return AjaxResult.success(iRoleToPermissionService.removeRoleToPermission(systemRoleToPermission));
                }
            }
            return AjaxResult.error("修改失败");
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


}
