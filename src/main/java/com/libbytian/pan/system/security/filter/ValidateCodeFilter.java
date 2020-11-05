package com.libbytian.pan.system.security.filter;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libbytian.pan.system.common.AjaxResult;
import com.libbytian.pan.system.exception.ImageCodeException;
import com.libbytian.pan.system.exception.TokenIsExpiredException;
import com.libbytian.pan.system.util.JwtTokenUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description: 图片验证码过滤器
 *
 * @author huangweicheng
 * @date 2019/10/22
 */
@Component
public class ValidateCodeFilter extends OncePerRequestFilter implements InitializingBean {
    /**
     * 哪些地址需要图片验证码进行验证
     */
    private Set<String> urls = new HashSet<>();

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    private RedisTemplate redisTemplate;


    public void setredisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        urls.add("/login/signin");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        boolean action = false;
        String requestURI = httpServletRequest.getRequestURI();
        String tokenHeader = httpServletRequest.getHeader(JwtTokenUtils.TOKEN_HEADER);

        // 如果请求头中没有Authorization信息则直接放行了
        if (tokenHeader == null || !tokenHeader.startsWith(JwtTokenUtils.TOKEN_PREFIX)) {
            for (String url : urls) {
                if (antPathMatcher.match(url, httpServletRequest.getRequestURI())) {
                    action = true;
                    break;
                }
            }
            if (action) {
                try {
                    /*图片验证码是否正确*/
                    checkImageCode(httpServletRequest);
                } catch (ImageCodeException e) {
                    httpServletResponse.setCharacterEncoding("UTF-8");
                    httpServletResponse.setContentType("application/json; charset=utf-8");
                    httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    String reason = "统一处理，原因：" + e.getMessage();
                    httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(AjaxResult.error(reason)));
                    httpServletResponse.getWriter().flush();
                    return;
                }
            }

            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        try {
            SecurityContextHolder.getContext().setAuthentication(getAuthentication(tokenHeader));
        } catch (TokenIsExpiredException e) {
               e.printStackTrace();
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     * Description:验证图片验证码是否正确
     *
     * @param httpServletRequest
     * @author huangweicheng
     * @date 2019/10/22
     */
    private void checkImageCode(HttpServletRequest httpServletRequest) {
        /*从cookie取值*/
        String uuid = httpServletRequest.getParameter("captcha");

        String redisImageCode = (String) redisTemplate.opsForValue().get(uuid);
        /*获取图片验证码与redis验证*/
        String imageCode = httpServletRequest.getParameter("imageCode");
        /*redis的验证码不能为空*/
        if (StringUtils.isEmpty(redisImageCode) || StringUtils.isEmpty(imageCode)) {
            throw new ImageCodeException("验证码不能为空");
        }
        /*校验验证码*/
        if (!imageCode.equalsIgnoreCase(redisImageCode)) {
            throw new ImageCodeException("验证码错误");
        }
        redisTemplate.delete(redisImageCode);
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

}