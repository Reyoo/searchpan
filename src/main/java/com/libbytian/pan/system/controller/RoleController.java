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
public class RoleController {

    private final IRoleService iRoleService;


    /**
     * 根据roleName，查询用户信息
     * @param start
     * @param limit
     * @param roleName
     * @return
     */
    @RequestMapping(value = "role/find",method = RequestMethod.GET)
    public AjaxResult findRole(@RequestParam int start , @RequestParam int limit , @RequestParam String roleName){

        Page<SystemRoleModel> page = new Page<>(start,limit);

        try {
            IPage<SystemUserModel> result = iRoleService.findRole(page,roleName);

            return AjaxResult.success(result);
        } catch (Exception e) {

            return AjaxResult.error(e.getMessage());
        }

    }


    /**
     * 新增角色
     * @param role
     * @return
     */
    @RequestMapping(value = "role/add",method = RequestMethod.POST)
    public AjaxResult addRole(@RequestBody SystemRoleModel role){

        try {
            iRoleService.addRole(role);

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }

    }

    /**
     * 根据角色ID修改角色名
     * @param role
     * @return
     */
    @RequestMapping(value = "role/put",method = RequestMethod.PUT)
    public AjaxResult putRole(@RequestBody SystemRoleModel role){

        try {
            iRoleService.putRole(role);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 根据角色ID删除角色
     * @param roleId
     * @return
     */
    @RequestMapping(value = "role/drop",method = RequestMethod.DELETE)
    public AjaxResult dropRole(@RequestParam String roleId){

        try {
            iRoleService.dropRole(roleId);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }



}
