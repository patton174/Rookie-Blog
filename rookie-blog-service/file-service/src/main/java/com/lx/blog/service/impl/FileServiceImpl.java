package com.lx.blog.service.impl;

import com.lx.blog.domain.dto.FileKeyDto;
import com.lx.blog.service.AbstractFileService;
import com.lx.blog.service.BaseFileService;
import com.lx.blog.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author LX
 * @date 2025/12/8
 * @description 文件服务实现类
 */
@Slf4j
@Service
public class FileServiceImpl extends AbstractFileService implements FileService {

    public FileServiceImpl(BaseFileService baseFileService) {
        super(baseFileService);
    }

    /**
     * 下载文件
     *
     * @param dto 文件键 dto
     * @return 文件字节数组
     */
    @Override
    public byte[] download(FileKeyDto dto) {
        // TODO: 这里需要实现文件流下载逻辑
        // 方案1：如果 BaseUpload 支持 getInputStream，则调用之
        // 方案2：使用 HttpUtils 通过 getUrl(dto) 下载 (不推荐用于大文件)
        // 方案3：各平台 Service 实现 specific download (目前 AbstractFileService 声明了 download 抽象方法，最终会调到这里)
        // 由于 BaseFileService (即 BaseUpload 的子类) 没有暴露 download 流的方法，
        // 建议在 BaseUpload 中增加 downloadStream 方法，或者在这里根据 URL 下载。
        
        String url = getUrl(dto);
        log.info("Downloading file from URL: {}", url);
        
        // 伪代码示例：
        // return HttpUtils.downloadBytes(url);
        
        throw new UnsupportedOperationException("文件流下载暂未实现，请直接访问 URL: " + url);
    }
}
