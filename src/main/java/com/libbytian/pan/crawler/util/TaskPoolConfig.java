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

    public static final int PROCESSORS=Runtime.getRuntime().availableProcessors();
    //线程最大的空闲存活时间，单位为秒
    public static final int KEEPALIVETIME=60;
    //任务缓存队列长度
    public static final int BLOCKINGQUEUE_LENGTH=500;

    @Bean("crawler-Executor")
    public Executor taskExecutro(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(PROCESSORS * 2);
        taskExecutor.setMaxPoolSize(PROCESSORS * 4);
        taskExecutor.setQueueCapacity(BLOCKINGQUEUE_LENGTH);
        taskExecutor.setKeepAliveSeconds(KEEPALIVETIME);
        taskExecutor.setThreadNamePrefix("crawler-Executor--");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        return taskExecutor;
    }


}