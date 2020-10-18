package com.libbytian.pan.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.*;
import com.libbytian.pan.system.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/role")
public class
RoleController {

    private final IRoleToPermissionService iRoleToPermissionService;
    private final ISystemUserToRoleService iSystemUserToRoleService;
    private final IPermissionService iPermissionService;
    private final ISystemRoleService iSystemRoleService;


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
                IPage<SystemRoleModel> result = iSystemRoleService.findRole(syspage, systemRoleModel);
                return AjaxResult.success(result);
            }else{
                return  AjaxResult.success(iSystemRoleService.list());
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
    public AjaxResult findRoleById(@RequestParam(defaultValue = "0") int start, @RequestParam(defaultValue = "10") int limit, @RequestParam String roleId) {

        Page<SystemRoleModel> page = new Page<>(start, limit);

        try {
            IPage<SystemRoleModel> result = iSystemRoleService.findRoleById(page, roleId);

            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 查询角色信息
     *
     * @param start
     * @param limit
     * @param systemRoleModel
     * @return
     */
    @RequestMapping(value = "/findrolebymodel", method = RequestMethod.POST)
    public AjaxResult findRole(@RequestParam(defaultValue = "1") int start, @RequestParam(defaultValue = "10") int limit, @RequestBody(required = false) SystemRoleModel systemRoleModel) {

        Page<SystemRoleModel> page = new Page<>(start, limit);

        try {

            IPage<SystemRoleModel> result = iSystemRoleService.findRole(page,systemRoleModel);

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
            iSystemRoleService.save(role);
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
            iSystemRoleService.updateById(role);
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
            if("1".equals(roleId) || "2".equals(roleId) || "3".equals(roleId)){
                return AjaxResult.error("该角色不可删除");
            }
            //查询用户角色表,判断角色是否绑定用户

            QueryWrapper queryWrapper = new QueryWrapper();

            queryWrapper.eq("role_id",roleId);
            int count = iSystemUserToRoleService.list(queryWrapper).size();

            if(count>0){
                return  AjaxResult.error("该角色已绑定用户，不可删除");
            }

            iSystemRoleService.dropRole(roleId);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }



    @RequestMapping(value = "/getauth", method = RequestMethod.POST)
    public AjaxResult finduserRole(@RequestBody SystemRoleModel systemRoleModel) {

        try {
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("role_id",systemRoleModel.getRoleId());
            //获取到角色管理权限的id集合
            List<SystemRoleToPermission> systemRoleToPermissionList = iRoleToPermissionService.list(queryWrapper);

            if(systemRoleToPermissionList.size()<=0){
                List<SystemPermissionModel> systemRoleToPermissions = iPermissionService.list();
                return AjaxResult.success(systemRoleToPermissions);
            }


            List<String> permissionId = systemRoleToPermissionList.stream().map(SystemRoleToPermission::getPermissionId).collect(Collectors.toList());
            List<SystemPermissionModel> systemRoleModelListAll = iPermissionService.listByIds(permissionId);
            systemRoleModelListAll.forEach(permissionModel -> permissionModel.setChecked(true));


            List<SystemPermissionModel> systemPermissionModelList = iPermissionService.list();
            //id为两个列表相同属性，取出A的list中的id
            List<String> permissionIdList =systemRoleModelListAll.stream().map(SystemPermissionModel::getPermissionId).collect(Collectors.toList());
            //B列表去除A列表已有的数据
            systemPermissionModelList =systemPermissionModelList.stream().filter(SystemRoleToPermission ->!permissionIdList.contains(SystemRoleToPermission.getPermissionId())).collect(Collectors.toList());
            systemPermissionModelList.addAll(systemRoleModelListAll);

            return AjaxResult.success(systemPermissionModelList);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }




    /**
     * 更新角色权限表表
     *
     * @param systemRoleToPermission
     * @return
     */
    @RequestMapping(value = "/updateauth", method = RequestMethod.PATCH)
    public AjaxResult updateuserRole(@RequestBody SystemRoleToPermission systemRoleToPermission) {


        try {
            if(systemRoleToPermission!=null){
                if(systemRoleToPermission.isChecked()){
                    //更新
                    return  AjaxResult.success(iRoleToPermissionService.save(systemRoleToPermission));
                }else{
                    //删除
                    QueryWrapper<SystemRoleToPermission> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("role_id", systemRoleToPermission.getRoleId());
                    queryWrapper.eq("permission_id", systemRoleToPermission.getPermissionId());
                    return AjaxResult.success(iRoleToPermissionService.remove(queryWrapper));
                }
            }
            return AjaxResult.error("修改失败");

        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }




}
