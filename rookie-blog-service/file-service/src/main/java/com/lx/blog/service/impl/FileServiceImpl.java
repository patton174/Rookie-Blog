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
        String url = getUrl(dto);
        log.info("Downloading file from URL: {}", url);
        
        try {
            return com.lx.blog.common.utils.HttpUtils.downloadBytes(url);
        } catch (java.io.IOException e) {
            log.error("Failed to download file from URL: {}", url, e);
            throw new com.lx.blog.common.base.BaseException("文件下载失败", e);
        }
    }
}
