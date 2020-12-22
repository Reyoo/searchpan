package com.libbytian.pan.system.controller;

import cn.hutool.core.util.StrUtil;
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

import java.time.LocalDate;
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
            if (systemRoleModel.getPage() != null) {
                page = Long.valueOf(systemRoleModel.getPage());
            }
            if (systemRoleModel.getLimits() != null) {
                limits = Long.valueOf(systemRoleModel.getLimits());
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
            role.setCreateTime(LocalDateTime.now());
            role.setRoleId(UUID.randomUUID().toString());
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

//            if ("ROLE_ADMIN".equals(role.getRoleName()) || "ROLE_NORMAL".equals(role.getRoleName()) || "ROLE_PAYUSER".equals(role.getRoleName())) {
//                return AjaxResult.error("该用户权限不允许修改");
//            }
//            iSystemRoleService.updateById(role);
//            return AjaxResult.success();

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
            if (StringUtils.isBlank(role.getRoleId())) {
                return AjaxResult.error("字段不能为空");
            }
//            if ("ROLE_ADMIN".equals(role.getRoleName()) || "ROLE_NORMAL".equals(role.getRoleName()) || "ROLE_PAYUSER".equals(role.getRoleName())) {
//                return AjaxResult.error("该用户权限不允许修改");
//            }

            if (!iSystemRoleService.checkRolerCouldDel(role)) {
                return AjaxResult.error("该角色为系统保留用户禁止删除");
            }

            //查询用户角色表,判断角色是否绑定用户
            SystemUserToRole systemUserToRole = new SystemUserToRole();
            systemUserToRole.setRoleId(role.getRoleId());
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
            systemRoleToPermission.setRoleId(systemRoleModel.getRoleId());
            //获取到角色管理权限的id集合
            List<SystemRoleToPermission> systemRoleToPermissionList = iRoleToPermissionService.listRoleToPermissionObjects(systemRoleToPermission);

            if (systemRoleToPermissionList.size() <= 0) {
                List<SystemPermissionModel> systemRoleToPermissions = iPermissionService.list();
                return AjaxResult.success(systemRoleToPermissions);
            }


            List<String> permissionId = systemRoleToPermissionList.stream().map(SystemRoleToPermission::getPermissionId).collect(Collectors.toList());
            List<SystemPermissionModel> systemRoleModelListAll = iPermissionService.listByIds(permissionId);
            systemRoleModelListAll.forEach(permissionModel -> permissionModel.setChecked(true));


            List<SystemPermissionModel> systemPermissionModelList = iPermissionService.list();
            //id为两个列表相同属性，取出A的list中的id
            List<String> permissionIdList = systemRoleModelListAll.stream().map(SystemPermissionModel::getPermissionId).collect(Collectors.toList());
            //B列表去除A列表已有的数据
            systemPermissionModelList = systemPermissionModelList.stream().filter(SystemRoleToPermission -> !permissionIdList.contains(SystemRoleToPermission.getPermissionId())).collect(Collectors.toList());
            systemPermissionModelList.forEach(permissionModel -> permissionModel.setChecked(false));
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
                if (systemRoleToPermission.getChecked()) {
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
