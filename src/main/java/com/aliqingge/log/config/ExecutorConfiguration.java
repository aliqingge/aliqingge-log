package com.aliqingge.log.config;

import com.aliqingge.log.collector.LogCollector;
import com.aliqingge.log.collector.impl.NothingCollector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.concurrent.Executor;

/**
 * 日志收集器执行器配置
 *
 * @author zhangzhongqing
 * @date 2020/9/27 14:47
 */
@EnableAsync
@Configuration
@ComponentScan
public class ExecutorConfiguration implements AsyncConfigurer {

    private static final Log log = LogFactory.getLog(ExecutorConfiguration.class);

    /**
     * 默认配置一个空的收集器
     */
    @Bean
    @ConditionalOnMissingBean(value = LogCollector.class)
    public LogCollector nothingCollector() {
        return new NothingCollector();
    }


    /**
     * 配置 日志收集器异步线程池
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(256);
        executor.setThreadNamePrefix("LogCollectorAsyncExecutor-");
        executor.setRejectedExecutionHandler((r, exec) ->
                log.error("LogCollectorAsyncExecutor thread queue is full,activeCount:" + exec.getActiveCount() + ",Subsequent collection tasks will be rejected,please check your LogCollector or config your Executor"));
        executor.initialize();
        return executor;
    }


    /**
     * AsyncUncaughtExceptionHandler
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> log.error("LogCollectorExecutor execution Exception [method: " + method + " ,params: " + Arrays.toString(params) + " ]", ex);
    }
}
