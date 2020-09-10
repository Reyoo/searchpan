package com.libbytian.pan.system.security.simple;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author niXueChao
 * @date 2019/4/3 23:05.
 */
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    /**
     * 登录失败 返回提示 权限框架完全运行后应当做跳转页面或者报文提示处理
     * @param request
     * @param response
     * @param exception
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write("onAuthenticationFailure --> " + exception.getMessage());
    }
}
