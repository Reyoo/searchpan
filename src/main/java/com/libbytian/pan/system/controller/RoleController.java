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

    @RequestMapping(value = "role/selct",method = RequestMethod.GET)
    public AjaxResult select(@RequestParam int start , @RequestParam int limit , @RequestBody SystemRoleModel role){

        Page<SystemRoleModel> page = new Page<>(start,limit);

        iRoleService.select(page,role);

        return AjaxResult.success();

    }

}
