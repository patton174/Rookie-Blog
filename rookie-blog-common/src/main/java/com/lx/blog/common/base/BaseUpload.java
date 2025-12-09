package com.lx.blog.common.base;

import com.lx.blog.common.config.model.StorageDomain;
import com.lx.blog.common.enums.StoragePlatformEnum;
import com.lx.blog.common.utils.FileUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

/**
 * @author LX
 * @date 2025/12/9
 * @description 基础上传抽象类 (定义上传策略规范)
 */
@Getter
@RequiredArgsConstructor
public abstract class BaseUpload {

    protected final StorageDomain storageDomain;

    /**
     * 获取存储平台类型
     *
     * @return 存储平台枚举
     */
    public abstract StoragePlatformEnum getPlatform();

    /**
     * 上传文件
     *
     * @param inputStream 文件流
     * @param path        相对路径 (如: avatar/2023/10/01/uuid.png)
     * @param contentType 文件类型
     * @return 访问URL
     */
    public abstract String upload(InputStream inputStream, String path, String contentType);

    /**
     * 删除文件
     *
     * @param path 相对路径
     * @return 是否成功
     */
    public abstract boolean delete(String path);

    /**
     * 获取文件访问URL
     *
     * @param path 相对路径
     * @return 完整URL
     */
    public abstract String getUrl(String path);

    /**
     * 检查文件是否存在
     *
     * @param path 相对路径
     * @return 是否存在
     */
    public abstract boolean exists(String path);

    /**
     * 生成完整的文件路径 (辅助方法)
     *
     * @param prefix   目录前缀
     * @param filename 原文件名
     * @return 相对路径
     */
    protected String generatePath(String prefix, String filename) {
        String dir = storageDomain.getFileDir();
        // 优先使用参数prefix，若为空则使用配置的全局目录
        if (prefix == null || prefix.isEmpty()) {
            prefix = dir;
        }
        return FileUtils.pathName(prefix, filename);
    }
}
