package com.libbytian.pan.system.controller;

import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemUserService;
import com.libbytian.pan.system.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author qisun
 * @date 2019/4/2 23:34.
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))

public class LoginController {

    private final ISystemUserService iUserService;


    /**
     * 登录跳转
     * @return
     */
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String dispathLogin() {
        return "/login/signin";
    }





    /**
     * 注册新用户
     *
     * @param user
     * @return
     */
    @RequestMapping(path = "/login/register", method = RequestMethod.POST)
    public AjaxResult loginRegister(@RequestBody SystemUserModel user) {
        try {
            int count = iUserService.selectByName(user.getUsername());

            if (count > 0) {
                return AjaxResult.error("该用户已存在,不可添加");
            }
            iUserService.register(user);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

}
