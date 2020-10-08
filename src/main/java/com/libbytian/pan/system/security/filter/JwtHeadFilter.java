package com.libbytian.pan.system.security.filter;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.libbytian.pan.system.exception.ImageCodeException;
import com.libbytian.pan.system.security.token.JwtLoginToken;
import com.libbytian.pan.system.security.token.JwtUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**拦截请求进行token验证
 * @author niXueChao
 * @date 2019/4/3 15:03.
 */
@Slf4j
public class JwtHeadFilter extends OncePerRequestFilter {
    private RsaVerifier verifier;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization");
        if (token==null || token.isEmpty()){
            filterChain.doFilter(request,response);
            return;
        }

        JwtUser user;
        try {
            Jwt jwt = JwtHelper.decodeAndVerify(token, verifier);
            String claims = jwt.getClaims();
            System.out.println(claims);
            user = JSON.parseObject(claims, JwtUser.class);
            //todo: 可以在这里添加检查用户是否过期,冻结...
        }catch (ImageCodeException e){
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("请检查验证码");
            return;
        } catch (Exception e){
            System.out.println(e.getMessage());
            //这里也可以filterChain.doFilter(request,response)然后return,那最后就会调用
            //.exceptionHandling().authenticationEntryPoint,也就是本列中的"需要登陆"
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("token 失效");
            return;
        }
        JwtLoginToken jwtLoginToken = new JwtLoginToken(user, "", user.getAuthorities());
        jwtLoginToken.setDetails(new WebAuthenticationDetails(request));
        SecurityContextHolder.getContext().setAuthentication(jwtLoginToken);
        filterChain.doFilter(request,response);
    }


    public void setVerifier(RsaVerifier verifier) {
        this.verifier = verifier;
    }



}
