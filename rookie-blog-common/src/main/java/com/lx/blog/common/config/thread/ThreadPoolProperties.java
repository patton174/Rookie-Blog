package com.lx.blog.common.config.thread;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author LX
 * @date 2025/12/10
 * @description 线程池配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "rookie.thread-pool")
public class ThreadPoolProperties {

    /**
     * 核心线程数
     */
    private Integer corePoolSize = 10;

    /**
     * 最大线程数
     */
    private Integer maxPoolSize = 20;

    /**
     * 队列容量
     */
    private Integer queueCapacity = 200;

    /**
     * 线程存活时间（秒）
     */
    private Integer keepAliveSeconds = 60;

    /**
     * 线程名前缀
     */
    private String threadNamePrefix = "rookie-async-";
}
