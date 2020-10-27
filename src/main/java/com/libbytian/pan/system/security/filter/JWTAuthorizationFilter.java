package com.libbytian.pan.system.security.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.libbytian.pan.system.exception.ImageCodeException;
import com.libbytian.pan.system.exception.TokenIsExpiredException;
import com.libbytian.pan.system.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by echisan on 2018/6/23
 */
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {


    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String tokenHeader = request.getHeader(JwtTokenUtils.TOKEN_HEADER);
        // 如果请求头中没有Authorization信息则直接放行了
        if (tokenHeader == null || !tokenHeader.startsWith(JwtTokenUtils.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        // 如果请求头中有token，则进行解析，并且设置认证信息
        try {
            SecurityContextHolder.getContext().setAuthentication(getAuthentication(tokenHeader));

        } catch (TokenIsExpiredException e) {
            //返回json形式的错误信息
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            String reason = "统一处理，原因：" + e.getMessage();
            response.getWriter().write(new ObjectMapper().writeValueAsString(reason));
            response.getWriter().flush();
            return;
        }
        super.doFilterInternal(request, response, chain);
    }

    /**
     * 这里从token中获取用户信息并新建一个token
     * @param tokenHeader
     * @return
     * @throws TokenIsExpiredException
     */
    private UsernamePasswordAuthenticationToken getAuthentication(String tokenHeader) throws TokenIsExpiredException {
        String token = tokenHeader.replace(JwtTokenUtils.TOKEN_PREFIX, "");
        boolean expiration = JwtTokenUtils.isExpiration(token);
        if (expiration) {
            throw new TokenIsExpiredException("token超时了");
        } else {
            String username = JwtTokenUtils.getUsername(token);
            List roles = JwtTokenUtils.getUserRole(token);
            String role = (String)roles.stream().collect(Collectors.joining(","));
            if (username != null) {
                return new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(new SimpleGrantedAuthority(role)));
            }
        }
        return null;
    }



//
//    private void validate(ServletWebRequest request) throws ServletRequestBindingException {
//        ImageCod codeInSession = (ImageCode) sessionStrategy.getAttribute(request,ValidateController.SESSION_KEY);
//        String codeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(),"imageCode");
//        if(!StringUtils.hasText(codeInRequest)){
//            throw new ImageCodeException("验证码的值不能为空！");
//        }
//        if(codeInSession == null){
//            throw new ImageCodeException("验证码不存在！");
//        }
//        if(codeInSession.isExpried()){
//            sessionStrategy.removeAttribute(request,ValidateController.SESSION_KEY);
//            throw new ImageCodeException("验证码已过期！");
//        }
//        if(!codeInSession.getCode().equals(codeInRequest)){
//            throw new ImageCodeException("验证码不正确！");
//        }
//        sessionStrategy.removeAttribute(request,ValidateController.SESSION_KEY);
//    }
//
//
//    /**
//     *
//     * Description:验证图片验证码是否正确
//     * @param httpServletRequest
//     * @author huangweicheng
//     * @date 2019/10/22
//     */
//    private void checkImageCode(HttpServletRequest httpServletRequest) throws ImageCodeException
//    {
//        /*从cookie取值*/
//        Cookie[] cookies = httpServletRequest.getCookies();
//        String uuid = "";
//        for (Cookie cookie : cookies)
//        {
//            String cookieName = cookie.getName();
//            if ("captcha".equals(cookieName))
//            {
//                uuid = cookie.getValue();
//            }
//        }
//        String redisImageCode = (String) redisTemplate.opsForValue().get(uuid);
//        /*获取图片验证码与redis验证*/
//        String imageCode = httpServletRequest.getParameter("imageCode");
//        /*redis的验证码不能为空*/
//        if (StringUtils.isEmpty(redisImageCode) || StringUtils.isEmpty(imageCode))
//        {
//            throw new ImageCodeException("验证码不能为空");
//        }
//        /*校验验证码*/
//        if (!imageCode.equalsIgnoreCase(redisImageCode))
//        {
//            throw new ImageCodeException("验证码错误");
//        }
//        redisTemplate.delete(redisImageCode);
//    }



}
