package com.libbytian.pan.system.controller;

import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author niXueChao
 * @date 2019/4/2 23:34.
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))

public class LoginController {

    private final ISystemUserService iUserService;

//    @GetMapping(value = "/login")
//    public String login() {
//        return "login";
//    }


    /**
     * 注册新用户
     * @param user
     * @return
     */
    @RequestMapping(path = "/login/register" ,method = RequestMethod.POST)
    public AjaxResult loginRegister(@RequestBody SystemUserModel user){
            try{
                iUserService.register(user);
                return  AjaxResult.success();
            }catch (Exception e){
                return  AjaxResult.error(e.getMessage());
            }
    }

}
