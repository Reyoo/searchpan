package com.libbytian.pan.system.aop;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;


/**
 * @author sun7127
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
//最高优先级
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface RequestLimit {

    /**
     * 调用的次数
     * @return
     */
    int count() default 5;

    /**
     * 时间段； 在time内调用的次数count -单位秒
     * @return
     */
    int frameTime() default 1;

    /**
     * 锁定时间 -单位小时
     * @return
     */
    int  lockTime() default  1;

}