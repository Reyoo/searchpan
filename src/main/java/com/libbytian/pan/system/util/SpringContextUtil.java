package com.libbytian.pan.system.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 该工具类用作在执行DispatcherServlet前注入bean
 * 例 systemUserMapper = SpringContextUtil.getBean("systemUserMapper");
 */
@Configuration
public class SpringContextUtil implements ApplicationContextAware {

    @Bean
    public SpringContextUtil setBean(){
        return new SpringContextUtil();
    }

    private static ApplicationContext applicationContext ;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    /**
     * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        checkApplicationContext();
        return (T) applicationContext.getBean(name);
    }

    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException(
                    "applicaitonContext未注入，请检查你的代码！");
        }
    }

}
