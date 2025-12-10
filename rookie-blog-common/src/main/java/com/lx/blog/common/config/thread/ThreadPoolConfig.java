package com.lx.blog.common.config.thread;

import com.alibaba.ttl.TtlRunnable;
import com.alibaba.ttl.threadpool.TtlExecutors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author LX
 * @date 2025/12/10
 * @description 线程池配置（集成TTL实现上下文无感传递）
 */
@Slf4j
@EnableAsync
@Configuration
@RequiredArgsConstructor
public class ThreadPoolConfig implements AsyncConfigurer {

    private final ThreadPoolProperties threadPoolProperties;

    @Bean("taskExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        executor.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());
        
        // 拒绝策略：CallerRunsPolicy
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 优雅停机
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        // 初始化
        executor.initialize();
        
        // 使用 TTL 包装 Executor，实现 TransmittableThreadLocal 的无感传递
        // 这里的 TtlExecutors.getTtlExecutor 实际上就是对 Executor 进行了装饰（代理）
        // 它可以确保父线程的上下文（如 MDC、UserContext 等使用 TTL 存储的变量）能传递给子线程
        return TtlExecutors.getTtlExecutor(executor);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("异步任务执行异常: method={}, params={}", method.getName(), params, ex);
        };
    }
}
