package com.libbytian.pan.system.security.filter;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.security.model.AuthenticationSuccessModel;
import com.libbytian.pan.system.security.provider.JwtUser;
import com.libbytian.pan.system.util.JwtTokenUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;


/**
 * 登录用户校验过滤
 */


@Slf4j
public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private ThreadLocal<Integer> threadLocal = new ThreadLocal<>();


    public JWTAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login/signin", "POST"));
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        String userName = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMeStat = request.getParameter("rememberMe");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userName, password);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetails(request));
        threadLocal.set(rememberMeStat == null ? 0 : Integer.valueOf(rememberMeStat));
        Authentication authenticatedToken = this.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);

        return authenticatedToken;

    }


    /**
     * 成功验证后调用的方法
     * 如果验证成功，就生成token并返回
     *
     * @param request
     * @param response
     * @param chain
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {

        JwtUser jwtUser = (JwtUser) authentication.getPrincipal();
        log.info("jwtUser:" + jwtUser.toString());
        boolean isRemember = threadLocal.get() == 1;

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        //签发token
        String token = JwtTokenUtils.createToken(jwtUser.getUsername(), roles, isRemember);
        AuthenticationSuccessModel authenticationSuccessModel = new AuthenticationSuccessModel();
        authenticationSuccessModel.setUsername(authentication.getName());
        authenticationSuccessModel.setToken(JwtTokenUtils.TOKEN_PREFIX + token);
        authenticationSuccessModel.setStatus(200);
        if (roles.contains("ROLE_ADMIN")) {
            //登录到系统管理
            authenticationSuccessModel.setRoute("index/userManagement");
            response.getWriter().write(JSON.toJSONString(authenticationSuccessModel));
        }

        /**
         * 付费用户
         */
        if (roles.contains("ROLE_PAYUSER")) {
            //登录到cms管理
            authenticationSuccessModel.setRoute("mainManagement");
            response.getWriter().write(JSON.toJSONString(authenticationSuccessModel));
        }

//        /**
//         * 未付费用户 HuangS
//         */
        if (roles.contains("ROLE_NORMAL")) {
            //登录到cms管理
            authenticationSuccessModel.setRoute("mainManagement");
            //移除token值
//            authenticationSuccessModel.setToken(null);
            response.getWriter().write(JSON.toJSONString(authenticationSuccessModel));
        }


        response.setHeader("token", JwtTokenUtils.TOKEN_PREFIX + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.error("authentication failed, reason: " + failed.getMessage());
        response.getWriter().write(JSONObject.toJSONString(AjaxResult.error("用户名或密码错误")));
    }

}
