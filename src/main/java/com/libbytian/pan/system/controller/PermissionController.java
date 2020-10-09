package com.libbytian.pan.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemPermissionModel;
import com.libbytian.pan.system.service.IPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/per")
public class PermissionController {

    private final IPermissionService iPermissionService;


    /**
     * 分页查询
     * @param page
     * @param limit
     * @param systemPermissionModel
     * @return
     */
    @RequestMapping(value = "/getper", method = RequestMethod.GET)
    public AjaxResult findPermission(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit, @RequestBody(required = false) SystemPermissionModel systemPermissionModel) {

        Page<SystemPermissionModel> systemPermissionModelPage = new Page<>(page, limit);
        try {
            IPage<SystemPermissionModel> result = iPermissionService.findPermission(systemPermissionModelPage, systemPermissionModel);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     *  更新
     * @param systemPermissionModel
     * @return
     */
    @RequestMapping(value = "/updateper", method = RequestMethod.PUT)
    public AjaxResult putPermission(@RequestBody SystemPermissionModel systemPermissionModel) {

        try {
            iPermissionService.updateById( systemPermissionModel);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }

    }

    /**
     * 删除
     *
     * @param permissionId
     * @return
     */
    @RequestMapping(value = "/dropper/{permissionId}", method = RequestMethod.DELETE)
    public AjaxResult dropPermission(@PathVariable  @NonNull String permissionId) {
        try {
            iPermissionService.dropPermission(permissionId);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }

    }

    /**
     * 添加
     *
     * @param permission
     * @return
     */
    @RequestMapping(value = "/addper", method = RequestMethod.POST)
    public AjaxResult addPermission(@RequestBody SystemPermissionModel permission) {

        try {
            int result = iPermissionService.addPermission(permission);
            if (result == 1) {
                return AjaxResult.success();
            } else {
                return AjaxResult.error();
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


}
