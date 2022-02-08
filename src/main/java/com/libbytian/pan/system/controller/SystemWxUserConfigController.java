package com.libbytian.pan.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.dto.SystemWxUserConfigDTO;

import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.model.SystemWxUserConfigModel;
import com.libbytian.pan.system.service.ISystemWxUserConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;

import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @Author:SunQi
 * @date : 2022/01/26 15:00
 * @Description: 用户微信信息接口类
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/sys/wx")
@Slf4j
public class SystemWxUserConfigController {

    private final ISystemWxUserConfigService systemWxUserConfigService;

    private final WxMpService wxMpService;

    @Value("${findfish.config.wechatFace}")
    String findfishWechatUrl;


    /**
     * 根据用户名查询
     * @param user
     * @return
     */
    @RequestMapping(value = "/findWXuser", method = RequestMethod.POST)
    public AjaxResult findWXUserWithUsername(@RequestBody(required = true) SystemUserModel user) {
        try {
            QueryWrapper<SystemWxUserConfigModel> queryWrapper = new QueryWrapper();
            queryWrapper.lambda().eq(SystemWxUserConfigModel::getUsername, user.getUsername());
            SystemWxUserConfigModel one = systemWxUserConfigService.getOne(queryWrapper);
            return AjaxResult.success(one);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    /**
     * 保存或更新
     * @param dto
     * @return
     */
    @RequestMapping(value = "/freshWXUser", method = RequestMethod.POST)
    public AjaxResult updateWXUser(@RequestBody(required = true) SystemWxUserConfigDTO dto) {
        try {
            if(StrUtil.isNotBlank(dto.getWxAppId())){
                dto.setUserSafeKey(findfishWechatUrl.concat(Base64.getEncoder().encodeToString(dto.getUsername().getBytes(StandardCharsets.UTF_8))).concat("/").concat(dto.getWxAppId()));
            }
            boolean b = systemWxUserConfigService.saveOrUpdate(dto);
            if (b) {
                WxMpDefaultConfigImpl configStorage = new WxMpDefaultConfigImpl();
                configStorage.setAppId(dto.getWxAppId());
                configStorage.setSecret(dto.getWxSecret());
                configStorage.setToken(dto.getWxToken());
                configStorage.setAesKey(dto.getWxAesKey());
                SystemWxUserConfigModel byId = systemWxUserConfigService.getById(dto.getUserId());
                if(byId !=null){
                    //更新操作
                    wxMpService.removeConfigStorage(dto.getWxAppId());
                }

                wxMpService.addConfigStorage(dto.getWxAppId(),configStorage);
                return AjaxResult.success();
            }
            return AjaxResult.error();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 根据用户名删除
     * @param user
     * @return
     */
    @RequestMapping(value = "/dropWXUser", method = RequestMethod.POST)
    public AjaxResult dropWXUser(@RequestBody(required = true) SystemUserModel user) {
        try {
            QueryWrapper<SystemWxUserConfigModel> queryWrapper = new QueryWrapper();
            queryWrapper.lambda().eq(SystemWxUserConfigModel::getUsername, user.getUsername());

            SystemWxUserConfigModel one = systemWxUserConfigService.getOne(queryWrapper);
            boolean b = systemWxUserConfigService.remove(queryWrapper);
            if (b) {
                if(StrUtil.isNotBlank(one.getWxAppId())){
                    wxMpService.removeConfigStorage(one.getWxAppId());
                }
                return AjaxResult.success();
            }
            return AjaxResult.error();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }



}
