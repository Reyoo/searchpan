package com.libbytian.pan.system.security.point;

/**
 * @author: QiSun
 * @date: 2020-09-29
 * @description:没有携带token或者token无效
 */

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.libbytian.pan.system.common.AjaxResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.alibaba.fastjson.JSONObject;


public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/javascript;charset=utf-8");
        response.getWriter().print(JSONObject.toJSONString(AjaxResult.error("请检查验证码")));
    }

}