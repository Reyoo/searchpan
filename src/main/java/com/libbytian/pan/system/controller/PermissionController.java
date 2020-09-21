package com.libbytian.pan.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemPermissionModel;
import com.libbytian.pan.system.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PermissionController {

    private final IPermissionService iPermissionService;


    /**
     * 分页查询
     * @param start
     * @param limit
     * @param username
     * @return
     */
    @RequestMapping(value = "per/find" , method = RequestMethod.GET)
    public AjaxResult findPermission(@RequestParam int start , @RequestParam int limit , @RequestParam String username){

        Page<SystemPermissionModel> page = new Page<>(start,limit);

        try {
            IPage<SystemPermissionModel>  result = iPermissionService.findPermission(page,username);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 修改  username未使用
     * @param username
     * @param body
     * @return
     */
    @RequestMapping(value = "per/put",method = RequestMethod.PUT)
    public AjaxResult putPermission(String username , @RequestBody SystemPermissionModel body){

        iPermissionService.putPermission(username,body);

        return AjaxResult.success();

    }

    /**
     * 删除
     * @param permissionId
     * @return
     */
    @RequestMapping(value = "per/drop" ,method = RequestMethod.DELETE)
    public AjaxResult dropPermission(@RequestParam String permissionId){

        iPermissionService.dropPermission(permissionId);
        return AjaxResult.success();
    }

    /**
     * 添加
     * @param permission
     * @return
     */
    @RequestMapping(value = "per/add",method = RequestMethod.POST)
    public AjaxResult addPermission(@RequestBody SystemPermissionModel permission){

       int result = iPermissionService.addPermission(permission);
       if(result==1){
           return AjaxResult.success();
       }else {
           return AjaxResult.error();
       }
    }


}
