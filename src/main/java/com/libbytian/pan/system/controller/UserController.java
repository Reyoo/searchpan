package com.libbytian.pan.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.*;
import com.libbytian.pan.system.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author yingzi
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final ISystemUserService iSystemUserService;
    private final ISystemUserToRoleService iSystemUserToRoleService;
    private final ISystemRoleService iSystemRoleService;


    /**
     * 条件查询 用户信息
     *
     * @param user = null 则为全查询
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    public AjaxResult findConditionByPage(@RequestBody(required = false) SystemUserModel user) {


        Long page = user.getPage()== null ? 1L:user.getPage();
        Long limits = user.getLimits() == null?10L :user.getLimits();

        Page<SystemUserModel> findpage = new Page<>(page, limits);
        try {
            IPage<SystemUserModel> result = iSystemUserService.findConditionByPage(findpage, user);
            return AjaxResult.success(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }

    }


    /**
     * 删除用户
     *
     * @param systemUserModel 多表删除用户ID数据
     *                        同时删除用户绑定角色
     *                        同时删除用户绑定模板
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public AjaxResult deleteUser(@RequestBody SystemUserModel systemUserModel) {

        try {
            if(iSystemUserService.checkUserCouldDel(systemUserModel)){
                iSystemUserService.removeUserAll(systemUserModel);
                return AjaxResult.success("delete success");
            }else {
                return AjaxResult.error("该用户为系统保留用户 禁止删除！！！");
            }

        } catch (Exception e) {
            return AjaxResult.error("delete error");
        }
    }

    /**
     * 更新用户
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.PATCH)
    public AjaxResult updateUser(@RequestBody SystemUserModel user) {

        try {

            if(iSystemUserService.checkUserCouldDel(user)){
                iSystemUserService.updateUser(user);
                return AjaxResult.success();
            }else {
                return AjaxResult.error("该用户为系统保留用户禁止修改");
            }


        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    @RequestMapping(value = "/geturole", method = RequestMethod.POST)
    public AjaxResult finduserRole(@RequestBody SystemUserModel user) {

        try {

            SystemUserToRole systemUserToRole = new SystemUserToRole();
            systemUserToRole.setUserId(user.getUserId());
            List<SystemUserToRole> systemUserToRoles = iSystemUserToRoleService.getUserToRoleObject(systemUserToRole);
            if (systemUserToRoles.size() <= 0) {
                List<SystemRoleModel> systemRoleModelsAll = iSystemRoleService.list();
                return AjaxResult.success(systemRoleModelsAll);
            }

            List<String> roleIds = systemUserToRoles.stream().map(SystemUserToRole::getRoleId).collect(Collectors.toList());
            List<SystemRoleModel> systemRoleModelList = iSystemRoleService.listByIds(roleIds);
            systemRoleModelList.forEach(role -> role.setChecked(true));
            List<SystemRoleModel> systemRoleModelsAll = iSystemRoleService.list();
            //id为两个列表相同属性，取出A的list中的id
            List<String> roleIdList = systemRoleModelList.stream().map(SystemRoleModel::getRoleId).collect(Collectors.toList());
            //B列表去除A列表已有的数据
            systemRoleModelsAll = systemRoleModelsAll.stream().filter(SystemRoleModel -> !roleIdList.contains(SystemRoleModel.getRoleId())).collect(Collectors.toList());
            systemRoleModelsAll.forEach(role -> role.setChecked(false));
            systemRoleModelsAll.addAll(systemRoleModelList);

            return AjaxResult.success(systemRoleModelsAll);
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 更新用户角色表
     * 传入 userId,roleId,checked(true or false)
     *
     * @param systemUserToRole
     * @return
     */
    @RequestMapping(value = "/updaterole", method = RequestMethod.PATCH)
    public AjaxResult updateuserRole(@RequestBody SystemUserToRole systemUserToRole) {

        try {
            if (systemUserToRole != null) {
                if (systemUserToRole.getChecked()) {
                    //更新
                    return AjaxResult.success(iSystemUserToRoleService.save(systemUserToRole));
                } else {
                    //删除
                    return AjaxResult.success(iSystemUserToRoleService.removeUserToRoleObject(systemUserToRole));
                }
            }
            return AjaxResult.error("修改失败");

        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }


//    /**
//     * 更新用户模板表
//     *
//     * @param systemUserToTemplates
//     * @return
//     */
//    @RequestMapping(value = "/freshusertemp", method = RequestMethod.POST)
//    public AjaxResult finduserTemplate(@RequestBody List<SystemUserToTemplate> systemUserToTemplates) {
//
//        //临时设置，更新用户模板表时把status默认设为0,如sun7有更好的实现，可删除
//        for (SystemUserToTemplate userToTemplate : systemUserToTemplates) {
//            userToTemplate.setUserTemplateStatus(false);
//        }
//        try {
//            iSystemUserToTemplateService.removeByIds(systemUserToTemplates.stream().map(SystemUserToTemplate::getUserTotemplateId).collect(Collectors.toList()));
//            return AjaxResult.success(iSystemUserToTemplateService.saveBatch(systemUserToTemplates));
//
//        } catch (Exception e) {
//            return AjaxResult.error(e.getMessage());
//        }
//    }


}
