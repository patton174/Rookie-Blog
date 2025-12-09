package com.lx.blog.common.config;

import com.lx.blog.common.config.model.StorageDomain;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LX
 * @date 2025/12/9
 * @description 存储配置类
 */
@Configuration
public class StorageConfig {

    @Bean
    @ConfigurationProperties(prefix = "storage")
    public StorageDomain storageDomain() {
        return new StorageDomain();
    }
}
