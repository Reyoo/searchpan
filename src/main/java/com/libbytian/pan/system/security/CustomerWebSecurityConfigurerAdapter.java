package com.libbytian.pan.system.security;


import com.libbytian.pan.system.security.filter.JWTAuthenticationFilter;
import com.libbytian.pan.system.security.filter.ValidateCodeFilter;
import com.libbytian.pan.system.security.filter.XssFilter;
import com.libbytian.pan.system.security.handle.LoginAccessDeineHandler;
import com.libbytian.pan.system.security.point.CustomAuthenticationEntryPoint;
import com.libbytian.pan.system.security.provider.JwtUserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @description： 登录配置
 * @Author : SunQi
 * @Date: 2020年10月9日
 */
@Configuration
public class CustomerWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtUserDetailServiceImpl jwtUserDetailService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    ValidateCodeFilter validateCodeFilter;
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        validateCodeFilter.setredisTemplate(redisTemplate);
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter();
        jwtAuthenticationFilter.setAuthenticationManager(this.authenticationManagerBean());
        http
                .authorizeRequests()
                //访问白名单
                .anyRequest ().access("@accessDecisionService.hasPermission(request , authentication)")
                .and()
                .addFilterAfter(new XssFilter(), CsrfFilter.class)
                //自定义验证
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(validateCodeFilter, JWTAuthenticationFilter.class)
                //异常处理
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                //添加无权限时的处理
                .accessDeniedHandler(new LoginAccessDeineHandler())
                .and()
                //禁用session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().cors().and()
                .csrf().disable().authorizeRequests()
                .and().logout().permitAll();

}

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfig = new CorsConfiguration();
        List<String> allowedHeaders = Arrays.asList("x-auth-token", "content-type", "X-Requested-With", "XMLHttpRequest","Access-Control-Allow-Origin","Authorization","authorization");
        List<String> exposedHeaders = Arrays.asList("x-auth-token", "content-type", "X-Requested-With", "XMLHttpRequest","Access-Control-Allow-Origin","Authorization","authorization");
        List<String> allowedMethods = Arrays.asList("POST", "GET", "DELETE", "PUT", "OPTIONS","PATCH");
        List<String> allowedOriginPatterns = Arrays.asList("*");
        corsConfig.setAllowedHeaders(allowedHeaders);
        corsConfig.setAllowedMethods(allowedMethods);
        corsConfig.setAllowedOriginPatterns(allowedOriginPatterns);
        corsConfig.setExposedHeaders(exposedHeaders);
        corsConfig.setMaxAge(36000L);
        corsConfig.setAllowCredentials(true);

        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfig);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
