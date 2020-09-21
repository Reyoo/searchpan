package com.libbytian.pan.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/user")
public class UserController {

    private final IUserService iUserService;


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
     * @param username
     * @return
     */
    @RequestMapping(value = "/delete",method = RequestMethod.DELETE)
    public AjaxResult deleteUuser(@RequestParam String username){

        try {
            iUserService.deleteUserByUsername(username);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 修改
     * @param user
     * @return
     */
    @RequestMapping(value = "/update",method = RequestMethod.PUT)
    public AjaxResult updateUser(@RequestBody SystemUserModel user) {

        try {
                iUserService.updateUser(user);
                return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
