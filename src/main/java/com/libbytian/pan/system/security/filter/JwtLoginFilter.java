package com.libbytian.pan.system.security.filter;

import com.alibaba.druid.util.StringUtils;
import com.libbytian.pan.system.exception.ImageCodeException;
import com.libbytian.pan.system.model.SystemRoleModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemUserService;
import com.libbytian.pan.system.util.PanHttpUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author niXueChao
 * @date 2019/4/2 14:07.
 */
@Slf4j
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter {


    @Autowired
    RedisTemplate redisTemplate;


    @Autowired
    ISystemUserService iSystemUserService;


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




//           iSystemRoleService = SpringContextUtil.getBean("iSystemRoleService");

           /**
            * 验证码
            */
//           checkImageCode(request);



           /**
            * 登录用户身份及过期验证
            */
           SystemUserModel systemUserModel = new SystemUserModel();
           systemUserModel.setUsername(userName);
//           iSystemUserService.checkUserStatus(systemUserModel);

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
           e.printStackTrace();
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



}
