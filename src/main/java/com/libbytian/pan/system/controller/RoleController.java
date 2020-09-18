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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleController {

    private final IRoleService iRoleService;


    /**
     * 根据角色名，查询用户信息
     * @param start
     * @param limit
     * @param roleName
     * @return
     */
    @RequestMapping(value = "role/find",method = RequestMethod.GET)
    public AjaxResult findUserByRole(@RequestParam int start , @RequestParam int limit , @RequestParam String roleName){
        Page<SystemRoleModel> page = new Page<>(start,limit);
        try {
            IPage<SystemUserModel> result = iRoleService.findUserByRole(page,roleName);

            return AjaxResult.success(result);
        } catch (Exception e) {

            return AjaxResult.error(e.getMessage());
        }

    }

    @RequestMapping(value = "/role/findRoleById",method = RequestMethod.GET)
    public AjaxResult findRoleById(@RequestParam int start ,@RequestParam int limit , @RequestParam String roleId){

        Page<SystemRoleModel> page =  new Page<>(start,limit);



        return AjaxResult.success();
    }


    /**
     * 新增角色
     * @param role
     * @return
     */
    @RequestMapping(value = "role/add",method = RequestMethod.POST)
    public AjaxResult addRole(@RequestBody SystemRoleModel role){

        String roleName = role.getRoleName();

        boolean flag = checkEmail(roleName);

        if(flag) {


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


        }else {
            return AjaxResult.error("角色名应为邮箱格式！");
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



    public boolean checkEmail(String roleName){
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(roleName);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;

        }
        return  flag;
    }


}
