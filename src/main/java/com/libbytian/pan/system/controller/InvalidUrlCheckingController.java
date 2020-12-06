package com.libbytian.pan.system.controller;

import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.service.impl.InvalidUrlCheckingService;
import com.libbytian.pan.system.model.MovieNameAndUrlModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.system.controller
 * @ClassName: InvalidUrlCheckingController
 * @Author: sun71
 * @Description: 失效链接检测
 * @Date: 2020/12/5 21:13
 * @Version: 1.0
 */
@RestController
@RequestMapping(value = "/invalid")
public class InvalidUrlCheckingController {

    @Autowired
    InvalidUrlCheckingService invalidUrlCheckingService;


    @RequestMapping(path = "/url", method = RequestMethod.POST)
    public AjaxResult checkInvalidUrl(@RequestBody List<MovieNameAndUrlModel> wangPanUrls) {

        try {
            for (MovieNameAndUrlModel movieNameAndUrlModel : wangPanUrls) {
                boolean isValid = invalidUrlCheckingService.checkUrlMethod(movieNameAndUrlModel.getWangPanUrl());

                if (isValid) {
                    return AjaxResult.success("链接失效");
                } else {
                    return AjaxResult.success("该链接请求正常");
                }
            }
        } catch (Exception e) {
            return AjaxResult.error("链接失效");
        }
        return AjaxResult.error("链接失效");
    }

}
