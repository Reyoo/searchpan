package com.libbytian.pan.crawler.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @ProjectName: pansearch
 * @Package: com.libbytian.pan.crawler.util
 * @ClassName: TaskPoolConfig
 * @Author: sun71
 * @Description:  线程池
 * @Date: 2020/12/13 11:59
 * @Version: 1.0
 */
@Configuration
@EnableAsync
public class TaskPoolConfig {

    @Bean("crawler-Executor")
    public Executor taskExecutro(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(50);
        taskExecutor.setQueueCapacity(200);
        taskExecutor.setKeepAliveSeconds(120);
        taskExecutor.setThreadNamePrefix("crawler-Executor--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        return taskExecutor;
    }


}