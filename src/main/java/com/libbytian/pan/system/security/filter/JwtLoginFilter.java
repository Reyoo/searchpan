package com.libbytian.pan.system.security.filter;

import com.alibaba.druid.util.StringUtils;
import com.libbytian.pan.system.exception.ImageCodeException;
import com.libbytian.pan.system.util.PanHttpUtil;
import com.libbytian.pan.system.mapper.SystemRoleMapper;
import com.libbytian.pan.system.mapper.SystemUserMapper;
import com.libbytian.pan.system.service.ISystemRoleService;
import com.libbytian.pan.system.service.ISystemUserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * @author niXueChao
 * @date 2019/4/2 14:07.
 */
@Slf4j
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {


    @Autowired
    RedisTemplate redisTemplate;




    /**
     * 设置登录的url 请求方式
     */
    public JwtLoginFilter() {
        super(new AntPathRequestMatcher("/login/signin", "POST"));
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
       try {
           String userName = request.getParameter("username");
           String password = request.getParameter("password");


//           systemUserMapper = SpringContextUtil.getBean("systemUserMapper");
//           systemRoleMapper = SpringContextUtil.getBean("systemRoleMapper");
//           iSystemRoleService = SpringContextUtil.getBean("iSystemRoleService");

           /**
            * 验证码
            */
//           checkImageCode(request);


           String realIp = PanHttpUtil.getIpAddress(request);
           System.out.println(request.getSession().getId());
           //创建未认证的凭证(etAuthenticated(false)),注意此时凭证中的主体principal为用户名
           JwtLoginToken jwtLoginToken = new JwtLoginToken(userName, password);

           //将认证详情(ip,sessionId)写到凭证
           jwtLoginToken.setDetails(new WebAuthenticationDetails(request));
           log.info(jwtLoginToken.toString());
           //AuthenticationManager获取受支持的AuthenticationProvider(这里也就是JwtAuthenticationProvider),
           //生成已认证的凭证,此时凭证中的主体为userDetails
           //放到redis 中设置过期时间

           Authentication authenticatedToken = this.getAuthenticationManager(). authenticate(jwtLoginToken);
           log.info(authenticatedToken.toString());
           return authenticatedToken;

       }catch (Exception e){
           throw new BadCredentialsException(e.getMessage());
       }
    }



    /**
     *
     * Description:验证图片验证码是否正确
     * @param httpServletRequest
     * @author huangweicheng
     * @date 2019/10/22
     */
    private void checkImageCode(HttpServletRequest httpServletRequest) throws ImageCodeException
    {
        /*从cookie取值*/
        Cookie[] cookies = httpServletRequest.getCookies();
        String uuid = "";
        for (Cookie cookie : cookies)
        {
            String cookieName = cookie.getName();
            if ("captcha".equals(cookieName))
            {
                uuid = cookie.getValue();
            }
        }
        String redisImageCode = (String) redisTemplate.opsForValue().get(uuid);
        /*获取图片验证码与redis验证*/
        String imageCode = httpServletRequest.getParameter("imageCode");
        /*redis的验证码不能为空*/
        if (StringUtils.isEmpty(redisImageCode) || StringUtils.isEmpty(imageCode))
        {
            throw new ImageCodeException("验证码不能为空");
        }
        /*校验验证码*/
        if (!imageCode.equalsIgnoreCase(redisImageCode))
        {
            throw new ImageCodeException("验证码错误");
        }
        redisTemplate.delete(redisImageCode);
    }


    /**
     * 判断是否为付费用户并且仍在付费使用期
     * @param username
     * @return
     */
//    private void isVip(String username) throws Exception {
//
//
//        //查询用户是否为付费用户
//
////      String[] userToRoleId = iSystemRoleService.getRoleIdByUsername(username);
//
//        String[] userToRoleId = systemRoleMapper.getRoleIdByUsername(username);
//
//        //查询用户权限到期时间
//        LocalDateTime actTime = systemUserMapper.findActTime(username);
//
//        //判断用户是否为付费用户
//        if( !Arrays.asList(userToRoleId).contains("3")){
//                throw new Exception("请升级到付费用户");
//        }
//
//        //判断付费用户是否到期
//        if (actTime.isBefore(LocalDateTime.now())){
//            throw new Exception("你的付费时长已过期，请续费使用");
//        }
//
//    }

}
