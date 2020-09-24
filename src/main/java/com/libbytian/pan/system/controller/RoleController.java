package com.libbytian.pan.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemRoleToPermission;
import com.libbytian.pan.system.model.SystemUserToRole;
import com.libbytian.pan.system.service.IRoleService;
import com.libbytian.pan.system.service.IRoleToPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/role")
public class RoleController {

    private final IRoleService iRoleService;
    private final IRoleToPermissionService iRoleToPermissionService;


    /**
     * 根据角色名，查询用户信息
     *
     * @param page
     * @param limit
     * @param systemRoleModel
     * @return
     */
    @RequestMapping(value = "/findrole", method = RequestMethod.GET)
    public AjaxResult findUserByRole(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "false") boolean isall,  @RequestBody(required = false) SystemRoleModel systemRoleModel) {
        Page<SystemRoleModel> syspage = new Page<>(page, limit);
        try {
            if(!isall){
                IPage<SystemRoleModel> result = iRoleService.findRole(syspage, systemRoleModel);
                return AjaxResult.success(result);
            }else{
                return  AjaxResult.success(iRoleService.list());
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }

    }

    /**
     * 根据角色ID，查询角色信息
     *
     * @param start
     * @param limit
     * @param roleId
     * @return
     */
    @RequestMapping(value = "/findrolebyid", method = RequestMethod.GET)
    public AjaxResult findRoleById(@RequestParam int start, @RequestParam int limit, @RequestParam String roleId) {

        Page<SystemRoleModel> page = new Page<>(start, limit);

        try {
            IPage<SystemRoleModel> result = iRoleService.findRoleById(page, roleId);

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
            role.setCreatetime(LocalDateTime.now());
            iRoleService.save(role);
            return AjaxResult.success();
        } catch (Exception e) {
            e.printStackTrace();
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
            if ("ROLE_ADMIN".equals(role.getRoleName()) || "ROLE_NORMAL".equals(role.getRoleName()) || "ROLE_PAYUSER".equals(role.getRoleName())) {
                return AjaxResult.error("该用户权限不允许修改");
            }
            iRoleService.updateById(role);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 根据角色ID删除角色
     *
     * @param roleId
     * @return
     */
    @RequestMapping(value = "/droprole/{roleId}", method = RequestMethod.DELETE)
    public AjaxResult dropRole(@PathVariable String roleId) {

        try {
            if(StrUtil.isBlank(roleId)){
                return  AjaxResult.error("字段不能为空");
            }
            iRoleService.dropRole(roleId);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 获取角色对应权限
     * @param role
     * @return
     */
    @RequestMapping(value = "/getroletopermission",method = RequestMethod.GET)
    public AjaxResult findrolePermission(@RequestBody SystemRoleModel role) {

        try {

             List<SystemRoleToPermission> systemRoleToPermissions =  iRoleToPermissionService.getRolePermissionByroleID(role.getRoleId());
             List<String> permissionIds  = systemRoleToPermissions.stream().map(SystemRoleToPermission::getPermissionId).collect(Collectors.toList());

            return AjaxResult.success(permissionIds);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }

    }


    /**
     * 更新角色权限
     * @param systemRoleToPermission
     * @return
     */
    @RequestMapping(value = "/addroletopermission",method = RequestMethod.POST)
    public AjaxResult findrolePermission(@RequestBody  List<SystemRoleToPermission>  systemRoleToPermission) {

        try {
            iRoleToPermissionService.removeByIds(systemRoleToPermission.stream().map(SystemRoleToPermission::getPermissionId).collect(Collectors.toList()));
            return AjaxResult.success(iRoleToPermissionService.saveBatch(systemRoleToPermission));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }



}
