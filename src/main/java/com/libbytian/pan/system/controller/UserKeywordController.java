package com.libbytian.pan.system.controller;


import cn.hutool.core.util.StrUtil;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemKeywordModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemKeywordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

/**
 * 用户关键字接口
 */


@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/userkey")
public class UserKeywordController {


    private final ISystemKeywordService iSystemKeywordService;



    @RequestMapping(value = "/list/{username}", method = RequestMethod.GET)
    public AjaxResult getUserKeywordByUser(@PathVariable String username) {

        try {
            return AjaxResult.success(iSystemKeywordService.getKeywordByUser(username));
        } catch (Exception e) {
            log.error( "error -> " + e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }


    @RequestMapping(value = "/fresh", method = RequestMethod.PATCH)
    public AjaxResult updateUserKeyword(HttpServletRequest httpRequest, @RequestBody(required = true) SystemKeywordModel systemKeywordModel) {

        try {
            String userSafeKey = null;
            if(StrUtil.isEmpty(systemKeywordModel.getAppId())){
                userSafeKey = "http://51.findfish.top/wechat/portal/"+ Base64.getEncoder().encodeToString(httpRequest.getRemoteUser().getBytes())+  "/" +"请填写appid";
                systemKeywordModel.setAppId("请填写appid");
            }else{
                userSafeKey= "http://51.findfish.top/wechat/portal/"+ Base64.getEncoder().encodeToString(httpRequest.getRemoteUser().getBytes())+  "/" + systemKeywordModel.getAppId();
            }

            systemKeywordModel.setUserSafeKey(userSafeKey);

            iSystemKeywordService.updateKeyword(systemKeywordModel);
            return AjaxResult.success("update is success !!! ");
        } catch (Exception e) {
            log.error("systemKeywordModel -- >" + systemKeywordModel.getKeywordId() + "error -> " + e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }



}
