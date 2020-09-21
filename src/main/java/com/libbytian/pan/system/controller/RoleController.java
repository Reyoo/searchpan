package com.libbytian.pan.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/role")
public class RoleController {

    private final IRoleService iRoleService;


    /**
     * 根据角色名，查询用户信息
     * @param page
     * @param limit
     * @param systemRoleModel
     * @return
     */
    @RequestMapping(value = "/findrole", method = RequestMethod.GET)
    public AjaxResult findUserByRole(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit, @RequestBody(required = false) SystemRoleModel systemRoleModel) {
        Page<SystemRoleModel> syspage = new Page<>(page, limit);
        try {
            IPage<SystemRoleModel> result = iRoleService.findRole(syspage, systemRoleModel);
            return AjaxResult.success(result);
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

//        boolean flag = iRoleService.checkEmail(roleName);


        try {
            int count = iRoleService.roleNameCount(role.getRoleName());
            if (count > 0) {
                return AjaxResult.error("角色名已存在，请重新输入");
            }

            iRoleService.addRole(role);

            return AjaxResult.success();
        } catch (Exception e) {
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
            if("ROLE_ADMIN".equals(role.getRoleName()) || "ROLE_NORMAL".equals(role.getRoleName()) || "ROLE_PAYUSER".equals(role.getRoleName())){
                return AjaxResult.error("该用户权限不允许修改");
            }
            iRoleService.putRole(role);
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
    @RequestMapping(value = "/droprole", method = RequestMethod.DELETE)
    public AjaxResult dropRole(@RequestParam String roleId) {

        try {
            iRoleService.dropRole(roleId);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

}
