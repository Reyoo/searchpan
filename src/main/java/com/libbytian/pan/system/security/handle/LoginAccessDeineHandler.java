package com.libbytian.pan.system.security.handle;

/**
 * @author: QiSun
 * @date: 2020-09-29
 * @description:没有访问权限
 */
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.libbytian.pan.system.common.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.alibaba.fastjson.JSONObject;

@Slf4j
public class LoginAccessDeineHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/javascript;charset=utf-8");
        log.error("权限不足，请联系管理员!");
        response.getWriter().print(JSONObject.toJSONString(AjaxResult.error("权限不足，请联系管理员!")));
    }

}