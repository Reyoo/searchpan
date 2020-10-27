package com.libbytian.pan.system.security.filter;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.security.model.AuthenticationSuccessModel;
import com.libbytian.pan.system.security.provider.JwtUser;
import com.libbytian.pan.system.util.JwtTokenUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 *
 */

@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private ThreadLocal<Integer> rememberMe = new ThreadLocal<>();
    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        super.setFilterProcessesUrl("/login/signin");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        // 从输入流中获取到登录的信息
        try {
            SystemUserModel systemUserModel = new ObjectMapper().readValue(request.getInputStream(), SystemUserModel.class);
            rememberMe.set(systemUserModel.getRememberMe() == null ? 0 : systemUserModel.getRememberMe());
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(systemUserModel.getUsername(), systemUserModel.getPassword(), new ArrayList<>())
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    /**
     * 成功验证后调用的方法
     * 如果验证成功，就生成token并返回
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
        System.out.println("jwtUser:" + jwtUser.toString());
        boolean isRemember = rememberMe.get() == 1;

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        //签发token
        String token = JwtTokenUtils.createToken(jwtUser.getUsername(), roles, isRemember);
        AuthenticationSuccessModel authenticationSuccessModel = new AuthenticationSuccessModel();
        authenticationSuccessModel.setUsername(authentication.getName());
        authenticationSuccessModel.setToken(token);
        if (roles.contains("ROLE_ADMIN")){

            //登录到系统管理
            authenticationSuccessModel.setRoute("index/userManagement");
            authenticationSuccessModel.setToken(token);
            authenticationSuccessModel.setStatus(200);
            response.getWriter().write(JSON.toJSONString(authenticationSuccessModel));
        }

        /**
         * 付费用户
         */
        if (roles.contains("ROLE_PAYUSER")){

            //登录到cms管理
            authenticationSuccessModel.setRoute("mainManagement");
            authenticationSuccessModel.setToken(token);
            authenticationSuccessModel.setStatus(200);
            response.getWriter().write(JSON.toJSONString(authenticationSuccessModel));
        }



        response.setHeader("token", JwtTokenUtils.TOKEN_PREFIX + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.getWriter().write("authentication failed, reason: " + failed.getMessage());
    }
}
