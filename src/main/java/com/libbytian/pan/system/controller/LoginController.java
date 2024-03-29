package com.libbytian.pan.system.controller;

import cn.hutool.core.util.ObjectUtil;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemUserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author qisun
 * @date 2019/4/2 23:34.
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class LoginController {

    private final ISystemUserService iSystemUserService;

    private final WxMpService  wxMpService;
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

            SystemUserModel userModel = new SystemUserModel();
            userModel.setUsername(systemUserModel.getUsername());
            SystemUserModel user = iSystemUserService.getUser(userModel);
            if (ObjectUtil.isNotEmpty(user)) {
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
