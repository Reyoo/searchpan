package com.libbytian.pan.system.security.simple;


import com.libbytian.pan.system.security.filter.JwtHeadFilter;
import com.libbytian.pan.system.security.filter.JwtLoginFilter;
import com.libbytian.pan.system.security.handle.LoginAccessDeineHandler;
import com.libbytian.pan.system.security.handle.LoginFailureHandler;
import com.libbytian.pan.system.security.handle.LoginSuccessHandler;
import com.libbytian.pan.system.security.point.CustomAuthenticationEntryPoint;
import com.libbytian.pan.system.security.provider.JwtAuthenticationProvider;
import com.libbytian.pan.system.security.provider.JwtUserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * @description： 登录配置
 * @Author : SunQi
 * @Date: 2020年10月9日
 */
@Configuration
public class CustomerWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    private RsaVerifier verifier;

    @Autowired
    private RsaSigner signer;

    @Autowired
    private JwtUserDetailServiceImpl jwtUserDetailService;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //登录过滤器
        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter();
        jwtLoginFilter.setAuthenticationManager(this.authenticationManagerBean());

        //登录成功和失败的操作
        LoginSuccessHandler loginSuccessHandler = new LoginSuccessHandler();
        loginSuccessHandler.setSigner(signer);
        jwtLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler);
        jwtLoginFilter.setAuthenticationFailureHandler(new LoginFailureHandler());

        //登录过滤器的授权提供者(就这么叫吧)
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(jwtUserDetailService);

        //JWT校验过滤器
        JwtHeadFilter headFilter = new JwtHeadFilter();
        headFilter.setVerifier(verifier);

        http
                //身份验证入口,当需要登录却没登录时调用
                //具体为,当抛出AccessDeniedException异常时且当前是匿名用户时调用
                //匿名用户: 当过滤器链走到匿名过滤器(AnonymousAuthenticationFilter)时,
                //会进行判断SecurityContext是否有凭证(Authentication),若前面的过滤器都没有提供凭证,
                //匿名过滤器会给SecurityContext提供一个匿名的凭证(可以理解为用户名和权限为anonymous的Authentication),
                //这也是JwtHeadFilter发现请求头中没有jwtToken不作处理而直接进入下一个过滤器的原因
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                //拒绝访问处理,当已登录,但权限不足时调用
                //抛出AccessDeniedException异常时且当不是匿名用户时调用
                .accessDeniedHandler(new LoginAccessDeineHandler())
                .and()
                .authorizeRequests()
                .anyRequest().access("@accessDecisionService.hasPermission(request , authentication)")
                .and()
                //将授权提供者注册到授权管理器中(AuthenticationManager)
                .authenticationProvider(provider)
                .addFilterAfter(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(headFilter, JwtLoginFilter.class)
                //禁用session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().cors().and()
                .csrf().disable().authorizeRequests()
                .and().logout().permitAll().and();

}

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
