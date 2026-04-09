package com.lx.blog.service.strategy;

import com.lx.blog.common.base.BaseUpload;
import com.lx.blog.common.config.model.StorageDomain;
import com.lx.blog.common.enums.StoragePlatformEnum;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @author LX
 * @date 2025/12/9
 * @description 七牛云文件存储服务实现
 */
@Slf4j
@Service
public class QiniuFileService extends BaseUpload {

    private final UploadManager uploadManager;
    private final BucketManager bucketManager;
    private final Auth auth;

    public QiniuFileService(StorageDomain storageDomain) {
        super(storageDomain);
        Configuration cfg = new Configuration(Region.autoRegion());
        this.uploadManager = new UploadManager(cfg);
        this.auth = Auth.create(storageDomain.getQiniuOssAccessKeyId(), storageDomain.getQiniuOssAccessKeySecret());
        this.bucketManager = new BucketManager(auth, cfg);
    }

    @Override
    public StoragePlatformEnum getPlatform() {
        return StoragePlatformEnum.QINIU;
    }

    @Override
    public String upload(InputStream inputStream, String path, String contentType) {
        try {
            String upToken = auth.uploadToken(storageDomain.getQiniuOssBucket());
            Response response = uploadManager.put(inputStream, path, upToken, null, contentType);
            if (response.isOK()) {
                return getUrl(path);
            } else {
                log.error("Qiniu upload failed, response: {}", response.bodyString());
                throw new RuntimeException("七牛云上传失败: " + response.bodyString());
            }
        } catch (QiniuException ex) {
            log.error("Qiniu upload exception", ex);
            throw new RuntimeException("七牛云上传异常", ex);
        }
    }

    @Override
    public boolean delete(String path) {
        try {
            bucketManager.delete(storageDomain.getQiniuOssBucket(), path);
            return true;
        } catch (QiniuException ex) {
            log.error("Qiniu delete exception", ex);
            return false;
        }
    }

    @Override
    public String getUrl(String path) {
        String domain = storageDomain.getQiniuOssUrl();
        if (domain != null && !domain.isEmpty()) {
            if (!domain.endsWith("/")) {
                domain += "/";
            }
            return domain + path;
        }
        return path;
    }

    @Override
    public boolean exists(String path) {
        try {
            bucketManager.stat(storageDomain.getQiniuOssBucket(), path);
            return true;
        } catch (QiniuException ex) {
            if (ex.code() == 612) { // 612 means file not exists
                return false;
            }
            log.error("Qiniu exists check exception", ex);
            return false;
        }
    }
}
