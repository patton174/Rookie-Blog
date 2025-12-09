package com.lx.blog.service.strategy;

import com.lx.blog.common.base.BaseUpload;
import com.lx.blog.common.config.model.StorageDomain;
import com.lx.blog.common.enums.StoragePlatformEnum;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @author LX
 * @date 2025/12/9
 * @description MinIO 文件存储服务实现
 */
@Service
public class MinioFileService extends BaseUpload {

    public MinioFileService(StorageDomain storageDomain) {
        super(storageDomain);
    }

    @Override
    public StoragePlatformEnum getPlatform() {
        return StoragePlatformEnum.MINIO;
    }

    @Override
    public String upload(InputStream inputStream, String path, String contentType) {
        // TODO: 实现 MinIO 上传逻辑
        return null;
    }

    @Override
    public boolean delete(String path) {
        // TODO: 实现 MinIO 删除逻辑
        return false;
    }

    @Override
    public String getUrl(String path) {
        // TODO: 实现 MinIO URL 获取逻辑
        return null;
    }

    @Override
    public boolean exists(String path) {
        // TODO: 实现 MinIO 文件存在检查逻辑
        return false;
    }
}
