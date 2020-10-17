package com.libbytian.pan.system.controller;

import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author qisun
 * @date 2019/4/2 23:34.
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class LoginController {

    private final ISystemUserService ISystemUserService;


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
            int count = ISystemUserService.selectByName(user.getUsername());

            if (count > 0) {
                return AjaxResult.error("该用户已存在,不可添加");
            }
            ISystemUserService.register(user);
            return AjaxResult.success();
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }

}
