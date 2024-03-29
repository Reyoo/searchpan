package com.libbytian.pan.system.security;

import com.libbytian.pan.system.model.SystemPermissionModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 配置路径访问限制,若你的用户角色比较简单,不需要存数据库,
 * 可以在ApplicationConfigurerAdapter里配置如
 * httpSecurity
 * .authorizeRequests()
 * .antMatchers("/order").....
 *
 * @author SunQi
 * @date 2019/4/10 10:33.
 */
@Component("accessDecisionService")
@Slf4j
public class AccessDecisionService {


    @Autowired
    ISystemPermissionService iSystemPermissionService;


    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public boolean hasPermission(HttpServletRequest request, Authentication auth) {

        List<String> whiteList = new ArrayList();
        /** 注册接口放过*/
        whiteList.add("/login/register");
        /** 微信通用接口 */
        whiteList.add("/wechat/portal/**");
        /** 验证码接口放过 */
        whiteList.add("/captcha/captchaImage");
//        whiteList.add("/userkey/**");
        whiteList.add("/fantasy/**");
        whiteList.add("/sys/wx/**");


        for (String url : whiteList) {
            if (antPathMatcher.match(url, request.getRequestURI())) {
                return true;
            }
        }
        if (auth instanceof AnonymousAuthenticationToken) {
            return false;
        }
        String username =  (String)auth.getPrincipal();
        SystemUserModel userModel = new SystemUserModel();
        userModel.setUsername(username.trim());
        List<String> urls = queryUrlByUser(userModel);
        for (String url : urls) {
            if (antPathMatcher.match(url, request.getRequestURI())) {
                return true;
            }
        }
//        if (auth instanceof AnonymousAuthenticationToken) {
//            return false;
//        }
        return false;
    }

    /**
     * 数据库查询用户权限
     * @param systemUserModel
     * @return
     */
    private List<String> queryUrlByUser(SystemUserModel systemUserModel) {
        try {
            /**
             * 根据用户获取权限entry
             */
            List<SystemPermissionModel> systemPermissionModelList = iSystemPermissionService.listPermissionByUser(systemUserModel);
            /**
             * 重排序获取url
             */
            List<String> urlList = systemPermissionModelList.stream().map(SystemPermissionModel::getPermissionUrl).collect(Collectors.toList());
            return urlList;
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }

    }
}
