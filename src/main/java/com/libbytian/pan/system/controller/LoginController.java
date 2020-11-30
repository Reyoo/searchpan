package com.libbytian.pan.system.controller;

import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemUserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author qisun
 * @date 2019/4/2 23:34.
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class LoginController {

    private final ISystemUserService iSystemUserService;


    /**
     * 登录跳转
     *
     * @return
     */
    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String dispathLogin() {
        return "/login/signin";
    }


    /**
     * 注册新用户
     *
     * @param systemUserModel
     * @return
     */
    @RequestMapping(path = "/login/register", method = RequestMethod.POST)
    public AjaxResult loginRegister(@RequestBody SystemUserModel systemUserModel) {
        try {
            //HuangS 2020.11.11
            //查询用户是否存在，只判断用户名,不传入密码
            SystemUserModel userModel = new SystemUserModel();
            userModel.setUsername(systemUserModel.getUsername());

            SystemUserModel user = iSystemUserService.getUser(userModel);

            if (user != null) {
                return AjaxResult.error("该用户已存在,不可添加");
            }
            iSystemUserService.register(systemUserModel);
            return AjaxResult.success();
        } catch (Exception e) {
            log.error(e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }


}
