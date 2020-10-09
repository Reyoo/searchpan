package com.libbytian.pan.system.controller;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.mapper.SystemUserToRoleMapper;
import com.libbytian.pan.system.model.SystemTemplateModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemUserToRole;
import com.libbytian.pan.system.model.SystemUserToTemplate;
import com.libbytian.pan.system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/user")
public class UserController {

    private final ISystemUserService iSystemUserService;
    private final ISystemUserToRoleService iSystemUserToRoleService;
    private final ISystemUserToTemplateService iSystemUserToTemplateService;
    private final ISystemTemplateService iSystemTemplateService;


    /**
     * 条件查询
     * @param page
     * @param limit
     * @param user = null 则为全查询
     * @return
     */
    @RequestMapping(value = "/select",method = RequestMethod.POST)
    public AjaxResult findConditionByPage(@RequestParam(defaultValue = "1")int page, @RequestParam(defaultValue = "10")  int limit, @RequestBody(required = false) SystemUserModel user){

        Page<SystemUserModel> findpage = new Page<>(page,limit);
        try {
            IPage<SystemUserModel> result = iSystemUserService.findConditionByPage(findpage,user);
            return  AjaxResult.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }

    }


    /**
     * 删除用户
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}",method = RequestMethod.DELETE)
    public AjaxResult deleteUser(@PathVariable String id){

        try {
            iSystemUserService.removeById(id);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 更新用户
     * @param user
     * @return
     */
    @RequestMapping(value = "/update",method = RequestMethod.PATCH)
    public AjaxResult updateUser(@RequestBody SystemUserModel user) {

        try {
            iSystemUserService.updateUser(user);
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


    /**
     * 更新用户角色表
     * @param systemUserToRole
     * @return
     */
    @RequestMapping(value = "/addusertorole",method = RequestMethod.POST)
    public AjaxResult finduserRole(@RequestBody  List<SystemUserToRole>  systemUserToRole) {

        try {
            iSystemUserToRoleService.removeByIds(systemUserToRole.stream().map(SystemUserToRole::getUserToRoleId).collect(Collectors.toList()));
            return AjaxResult.success(iSystemUserToRoleService.saveBatch(systemUserToRole));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 获取用户所有的模板
     * @return
     */
    @RequestMapping(value = "getusertemplate", method =RequestMethod.GET)
    public AjaxResult getuserTemplate(@RequestParam String username){

        try {
            List<SystemTemplateModel> result = iSystemUserService.findTemplateById(username);


            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }

    }



    /**
     * 更新用户模板表
     * @param systemUserToTemplates
     * @return
     */
    @RequestMapping(value = "/addusertotemplate",method = RequestMethod.POST)
    public AjaxResult finduserTemplate(@RequestBody  List<SystemUserToTemplate>  systemUserToTemplates) {

        //临时设置，更新用户模板表时把status默认设为0,如sun7有更好的实现，可删除
        for (SystemUserToTemplate userToTemplate : systemUserToTemplates) {
            userToTemplate.setUserTemplateStatus("0");
        }
        try {
            iSystemUserToTemplateService.removeByIds(systemUserToTemplates.stream().map(SystemUserToTemplate::getUserTotemplateId).collect(Collectors.toList()));
            return AjaxResult.success(iSystemUserToTemplateService.saveBatch(systemUserToTemplates));

        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }




    /**
     * 新增用户模板表
     * @param
     * @return
     */
    @RequestMapping(value = "/addtemplatetouser",method = RequestMethod.POST)
    @Transactional
    public AjaxResult finduserTemplate(HttpServletRequest httpRequest, @RequestBody(required = true) SystemTemplateModel  systemTemplateModel) {

        try {
            String uuid = UUID.randomUUID().toString();
            systemTemplateModel.setTemplateid(uuid);
            iSystemTemplateService.save(systemTemplateModel);



            /**
             * 插入模板 后  用户绑定 用户模板表
             */
            String user =  httpRequest.getRemoteUser();
            iSystemUserService.findByUsername(user);



            return AjaxResult.success();

        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }








}
