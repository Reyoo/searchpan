package com.libbytian.pan.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToRole;
import com.libbytian.pan.system.service.ISystemUserToRoleService;
import com.libbytian.pan.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/user")
public class UserController {

    private final IUserService iUserService;
    private  final ISystemUserToRoleService iSystemUserToRoleService;


    /**
     * 条件查询
     * @param page
     * @param limit
     * @param user = null 则为全查询
     * @return
     */
    @RequestMapping(value = "/select",method = RequestMethod.GET)
    public AjaxResult findConditionByPage(@RequestParam(defaultValue = "1")int page, @RequestParam(defaultValue = "10")  int limit, @RequestBody(required = false) SystemUserModel user){

        Page<SystemUserModel> findpage = new Page<>(page,limit);
        try {
            IPage<SystemUserModel> result = iUserService.findConditionByPage(findpage,user);
            return  AjaxResult.success(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }

    }


    /**
     * 删除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}",method = RequestMethod.DELETE)
    public AjaxResult deleteUser(@PathVariable String id){

        try {
            iUserService.removeById(id);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 更细用户
     * @param user
     * @return
     */
    @RequestMapping(value = "/update",method = RequestMethod.PATCH)
    public AjaxResult updateUser(@RequestBody SystemUserModel user) {

        try {
                iUserService.updateUser(user);
                return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }



    /**
     * 获取用户角色
     * @param user
     * @return
     */
    @RequestMapping(value = "/geturole",method = RequestMethod.GET)
    public AjaxResult finduserRole(@RequestBody SystemUserModel user) {

        try {

            List<SystemUserToRole> systemUserToRoles =  iSystemUserToRoleService.getUserRoleByuserID(user.getUserId());
            List<String> roleIds = systemUserToRoles.stream().map(SystemUserToRole::getRoleId).collect(Collectors.toList());
            return AjaxResult.success(roleIds);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


}
