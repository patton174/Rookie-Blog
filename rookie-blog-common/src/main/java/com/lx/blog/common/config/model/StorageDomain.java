package com.lx.blog.common.config.model;

import lombok.Data;

/**
 * @author LX
 * @date 2025/11/14
 * @description 存储域名实体类
 */
@Data
public class StorageDomain {

    /**
     * 文件存储方式(1:MinIO、2:七牛云)
     */
    private Integer storagePlatform;

    /**
     * 上传文件目录（使用平台bucket时区别不同文件）
     */
    private String fileDir;

    /**
     * 文档预览
     */
    private String filePreviewUrl;

    /**
     * Minio
     */
    private String minioEndpoint;
    private String minioAccessKey;
    private String minioSecretKey;
    private String minioFileDomain;
    private String minioBucket;

    /**
     * 七牛云RegionId
     */
    private String qiniuOssRegionId;

    /**
     * 七牛云
     */
    private String qiniuOssEndpoint;
    private String qiniuOssAccessKeyId;
    private String qiniuOssAccessKeySecret;
    private String qiniuOssUrl;
    private String qiniuOssBucket;

    /**
     * 项目名称
     */
    private String qiniuOssImmProjectName;
}
