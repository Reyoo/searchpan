package com.libbytian.pan.system.controller;


import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.model.SystemKeywordModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemKeywordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户关键字接口
 */


@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
@RequestMapping("/userkey")
public class UserKeywordController {


    private final ISystemKeywordService iSystemKeywordService;



    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public AjaxResult getUserKeywordByUser(@RequestBody(required = true) SystemUserModel user) {

        try {
            return AjaxResult.success(iSystemKeywordService.getKeywordByUser(user.getUserId()));
        } catch (Exception e) {
            log.error("userId -- >" + user.getUserId() + "error -> " + e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }


    @RequestMapping(value = "/fresh", method = RequestMethod.PATCH)
    public AjaxResult updateUserKeyword(@RequestBody(required = true) SystemKeywordModel systemKeywordModel) {

        try {
            iSystemKeywordService.updateKeyword(systemKeywordModel);
            return AjaxResult.success("update is success !!! call me baba ~");
        } catch (Exception e) {
            log.error("systemKeywordModel -- >" + systemKeywordModel.getKeywordId() + "error -> " + e.getMessage());
            return AjaxResult.error(e.getMessage());
        }
    }



}
