package com.lx.blog.service.strategy;

import com.lx.blog.common.base.BaseUpload;
import com.lx.blog.common.config.model.StorageDomain;
import com.lx.blog.common.enums.StoragePlatformEnum;
import io.minio.*;
import io.minio.errors.*;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @author LX
 * @date 2025/12/9
 * @description MinIO 文件存储服务实现
 */
@Slf4j
@Service
public class MinioFileService extends BaseUpload {

    @NotNull final MinioClient minioClient;

    public MinioFileService(StorageDomain storageDomain) {
        super(storageDomain);
        this.minioClient = MinioClient.builder()
                .endpoint(storageDomain.getMinioEndpoint())
                .credentials(storageDomain.getMinioAccessKey(), storageDomain.getMinioSecretKey())
                .build();
    }

    /**
     * 获取存储平台类型
     *
     * @return 存储平台枚举
     */
    @Override
    public StoragePlatformEnum getPlatform() {
        return StoragePlatformEnum.MINIO;
    }

    /**
     * 上传文件
     *
     * @param inputStream 文件流
     * @param path        相对路径 (如: avatar/2023/10/01/uuid.png)
     * @param contentType 文件类型
     * @return 访问URL
     */
    @Override
    public String upload(InputStream inputStream, String path, String contentType) {
        try {
            String bucketName = storageDomain.getMinioBucket();
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path)
                            .stream(inputStream, -1, 10485760) // -1 for unknown size, part size 10MB
                            .contentType(contentType)
                            .build());

            return getUrl(path);
        } catch (Exception e) {
            log.error("MinIO upload error: {}", e.getMessage(), e);
            throw new RuntimeException("MinIO upload failed", e);
        }
    }

    /**
     * 删除文件
     *
     * @param path 相对路径
     * @return 是否成功
     */
    @Override
    public boolean delete(String path) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(storageDomain.getMinioBucket())
                            .object(path)
                            .build());
            return true;
        } catch (Exception e) {
            log.error("MinIO delete error: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取文件访问URL
     *
     * @param path 相对路径
     * @return 完整URL
     */
    @Override
    public String getUrl(String path) {
        String domain = storageDomain.getMinioFileDomain();
        if (domain != null && !domain.isEmpty()) {
            if (!domain.endsWith("/")) {
                domain += "/";
            }
            return domain + storageDomain.getMinioBucket() + "/" + path;
        }
        // Fallback to endpoint if domain is not configured
        String endpoint = storageDomain.getMinioEndpoint();
         if (!endpoint.endsWith("/")) {
            endpoint += "/";
        }
        return endpoint + storageDomain.getMinioBucket() + "/" + path;
    }

    /**
     * 检查文件是否存在
     *
     * @param path 相对路径
     * @return 是否存在
     */
    @Override
    public boolean exists(String path) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(storageDomain.getMinioBucket())
                            .object(path)
                            .build());
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            log.error("MinIO exists check error: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("MinIO exists check error: {}", e.getMessage(), e);
            return false;
        }
    }
}
