package com.libbytian.pan.system.security.simple;

import com.alibaba.fastjson.JSON;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.security.model.AuthenticationSuccessModel;
import com.libbytian.pan.system.service.ISystemRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**登录成功
 * @author niXueChao
 * @date 2019/3/12.
 */
@Component
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
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());


//        if (roles.contains("ROLE_ADMIN")){
            //签发token
            AuthenticationSuccessModel authenticationSuccessModel = new AuthenticationSuccessModel();
            authenticationSuccessModel.setUsername(authentication.getName());
            authenticationSuccessModel.setRoute("userManagement");
            authenticationSuccessModel.setToken(token);
            authenticationSuccessModel.setStatus(0);
            response.getWriter().write(JSON.toJSONString(authenticationSuccessModel));
//        }
//        iSystemRoleService.getRolenameByUserId("15");
        /**
         * 判断用户权限： 设置路由
         * 封装map 为实体类
         * ROLE_ADMIN 返回
         */

    }

    public void setSigner(RsaSigner signer) {
        this.signer = signer;
    }
}
