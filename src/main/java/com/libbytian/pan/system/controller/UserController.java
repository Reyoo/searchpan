package com.libbytian.pan.system.controller;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.*;
import com.libbytian.pan.system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/user")
public class UserController {

    private final ISystemUserService iSystemUserService;
    private final ISystemUserToRoleService iSystemUserToRoleService;
    private final ISystemUserToTemplateService iSystemUserToTemplateService;
    private final ISystemTmplToTmplDetailsService iSystemTmplToTmplDetailsService;
    private final ISystemTemplateService iSystemTemplateService;
    private final ISystemRoleService iSystemRoleService;


    /**
     * 条件查询 用户信息
     *
     * @param page
     * @param limit
     * @param user  = null 则为全查询
     * @return
     */
    @RequestMapping(value = "/select", method = RequestMethod.POST)
    public AjaxResult findConditionByPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit, @RequestBody(required = false) SystemUserModel user) {

        Page<SystemUserModel> findpage = new Page<>(page, limit);
        try {
            IPage<SystemUserModel> result = iSystemUserService.findConditionByPage(findpage, user);
            return AjaxResult.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(e.getMessage());
        }

    }


    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public AjaxResult deleteUser(@PathVariable String id) {

        try {
            iSystemUserService.removeById(id);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
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
            iSystemUserService.updateUser(user);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 获取用户角色
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/geturole", method = RequestMethod.POST)
    public AjaxResult finduserRole(@RequestBody SystemUserModel user) {

        try {

            List<SystemUserToRole> systemUserToRoles = iSystemUserToRoleService.getUserRoleByuserID(user.getUserId());
            if(systemUserToRoles.size()<=0){
                List<SystemRoleModel> systemRoleModelsAll = iSystemRoleService.list();
                return AjaxResult.success(systemRoleModelsAll);
            }

            List<String> roleIds = systemUserToRoles.stream().map(SystemUserToRole::getRoleId).collect(Collectors.toList());
            List<SystemRoleModel> systemRoleModelList = iSystemRoleService.listByIds(roleIds);
            systemRoleModelList.forEach(role -> role.setChecked(true));
            List<SystemRoleModel> systemRoleModelsAll = iSystemRoleService.list();
            //id为两个列表相同属性，取出A的list中的id
            List<String> roleIdList =systemRoleModelList.stream().map(SystemRoleModel::getRoleId).collect(Collectors.toList());
            //B列表去除A列表已有的数据
            systemRoleModelsAll =systemRoleModelsAll.stream().filter(SystemRoleModel ->!roleIdList.contains(SystemRoleModel.getRoleId())).collect(Collectors.toList());

            systemRoleModelsAll.addAll(systemRoleModelList);


            return AjaxResult.success(systemRoleModelsAll);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 更新用户角色表
     *
     * @param systemUserToRole
     * @return
     */
    @RequestMapping(value = "/updaterole", method = RequestMethod.PATCH)
    public AjaxResult updateuserRole(@RequestBody SystemUserToRole systemUserToRole) {


        try {
            if(systemUserToRole!=null){
                if(systemUserToRole.isChecked()){
                    //更新
                    return  AjaxResult.success(iSystemUserToRoleService.save(systemUserToRole));
                }else{
                    //删除
                    QueryWrapper<SystemUserToRole> queryWrapper = new QueryWrapper<>();

                    queryWrapper.eq("user_id", systemUserToRole.getUserId());
                    queryWrapper.eq("role_id", systemUserToRole.getRoleId());
                    return AjaxResult.success(iSystemUserToRoleService.remove(queryWrapper));
                }
            }
            return AjaxResult.error("修改失败");

        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 获取用户所有的模板
     *
     * @return
     */
    @RequestMapping(value = "getusertemplate", method = RequestMethod.GET)
    public AjaxResult getuserTemplate(@RequestParam String username) {

        try {
            List<SystemTemplateModel> result = iSystemUserService.findTemplateById(username);
            List<SystemTemplateModel> oldResult = new ArrayList<>();
            for (SystemTemplateModel systemTemplateModel : result) {
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("template_id", systemTemplateModel.getTemplateid());
                systemTemplateModel.setDetialsize(iSystemTmplToTmplDetailsService.count(queryWrapper));
                oldResult.add(systemTemplateModel);
            }
            return AjaxResult.success(oldResult);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 更新用户模板表
     *
     * @param systemUserToTemplates
     * @return
     */
    @RequestMapping(value = "/addusertotemplate", method = RequestMethod.POST)
    public AjaxResult finduserTemplate(@RequestBody List<SystemUserToTemplate> systemUserToTemplates) {

        //临时设置，更新用户模板表时把status默认设为0,如sun7有更好的实现，可删除
        for (SystemUserToTemplate userToTemplate : systemUserToTemplates) {
            userToTemplate.setUserTemplateStatus(false);
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
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/addtemplatetouser", method = RequestMethod.POST)
    @Transactional
    public AjaxResult finduserTemplate(HttpServletRequest httpRequest, @RequestBody(required = true) SystemTemplateModel systemTemplateModel) {

        try {
            String uuid = UUID.randomUUID().toString();
            systemTemplateModel.setTemplateid(uuid);
            iSystemTemplateService.save(systemTemplateModel);
            systemTemplateModel.setTemplatecreatetime(LocalDateTime.now());

            /**
             * 插入模板 后  用户绑定 用户模板表
             */
            String user = httpRequest.getRemoteUser();
            SystemUserModel systemUserModel = iSystemUserService.findByUsername(user);
            SystemUserToTemplate systemUserToTemplate = new SystemUserToTemplate();
            systemUserToTemplate.setUserId(systemUserModel.getUserId());
            systemUserToTemplate.setTemplateId(uuid);
            systemUserToTemplate.setUserTemplateStatus(true);
            iSystemUserToTemplateService.save(systemUserToTemplate);
            return AjaxResult.success();

        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


}
