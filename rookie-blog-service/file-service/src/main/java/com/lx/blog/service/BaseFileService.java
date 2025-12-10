package com.lx.blog.service;

import com.lx.blog.common.base.BaseUpload;
import com.lx.blog.common.config.model.StorageDomain;
import com.lx.blog.common.enums.StoragePlatformEnum;
import com.lx.blog.common.exception.SystemErrorException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LX
 * @date 2025/12/9
 * @description 文件服务策略分发类 (策略模式上下文)
 */
@Service
@Primary
public class BaseFileService extends BaseUpload {

    private final Map<StoragePlatformEnum, BaseUpload> uploadStrategyMap = new ConcurrentHashMap<>();

    public BaseFileService(StorageDomain storageDomain, List<BaseUpload> uploadList) {
        super(storageDomain);
        // 初始化策略 Map
        if (uploadList != null) {
            for (BaseUpload upload : uploadList) {
                // 排除自己，避免循环引用
                if (upload instanceof BaseFileService) {
                    continue;
                }
                uploadStrategyMap.put(upload.getPlatform(), upload);
            }
        }
    }

    /**
     * 获取当前启用的上传策略
     *
     * @return 当前策略实例
     */
    protected BaseUpload getCurrentStrategy() {
        Integer platformCode = storageDomain.getStoragePlatform();
        StoragePlatformEnum platform = StoragePlatformEnum.byCode(platformCode);
        if (platform == null) {
            throw new SystemErrorException("未配置有效的文件存储平台: " + platformCode);
        }
        BaseUpload strategy = uploadStrategyMap.get(platform);
        if (strategy == null) {
            throw new SystemErrorException("未找到对应的文件存储策略实现: " + platform.getDesc());
        }
        return strategy;
    }

    @Override
    public StoragePlatformEnum getPlatform() {
        return getCurrentStrategy().getPlatform();
    }

    @Override
    public String upload(InputStream inputStream, String path, String contentType) {
        return getCurrentStrategy().upload(inputStream, path, contentType);
    }

    @Override
    public boolean delete(String path) {
        return getCurrentStrategy().delete(path);
    }

    @Override
    public String getUrl(String path) {
        return getCurrentStrategy().getUrl(path);
    }

    @Override
    public boolean exists(String path) {
        return getCurrentStrategy().exists(path);
    }
}
