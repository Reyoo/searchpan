package com.libbytian.pan.system.security.simple;

import com.alibaba.fastjson.JSON;
import com.libbytian.pan.system.common.AjaxResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**登录成功
 * @author niXueChao
 * @date 2019/3/12.
 */
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private RsaSigner signer;

    /**
     * 登录成功返回token
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        String userJsonStr = JSON.toJSONString(authentication.getPrincipal());
        String token = JwtHelper.encode(userJsonStr, signer).getEncoded();


        /**
         * 判断用户权限： 设置路由
         * 封装map 为实体类
         * ROLE_ADMIN 返回
         */
        //签发token
        Map map = new HashMap();
        map.put("username", authentication.getName());
        map.put("route", "userManagement");
        map.put("token", token);
        map.put("status",0);


        response.getWriter().write(JSON.toJSONString(map));
    }

    public void setSigner(RsaSigner signer) {
        this.signer = signer;
    }
}
