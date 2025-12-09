package com.lx.blog.service;

import com.lx.blog.common.base.BaseUpload;
import com.lx.blog.domain.dto.FileKeyDto;
import com.lx.blog.domain.dto.FileUploadDto;
import com.lx.blog.domain.vo.FileVo;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;

/**
 * @author LX
 * @date 2025/12/8
 * @description 文件服务抽象类 (连接 FileService 与 BaseUpload)
 */
@RequiredArgsConstructor
public abstract class AbstractFileService implements FileService {

    protected final BaseUpload baseUpload;

    /**
     * 上传文件
     *
     * @param dto 文件上传 dto
     * @return 文件 vo
     */
    @Override
    public final FileVo upload(FileUploadDto dto) {
        String url = baseUpload.upload(
                new ByteArrayInputStream(dto.getContent()),
                dto.getKey(),
                dto.getContentType()
        );
        
        FileVo vo = new FileVo();
        vo.setKey(dto.getKey());
        vo.setUrl(url);
        vo.setContentType(dto.getContentType());
        vo.setSize(dto.getContentLength());
        return vo;
    }

    /**
     * 删除文件
     *
     * @param dto 文件键 dto
     * @return 是否删除成功
     */
    @Override
    public final boolean delete(FileKeyDto dto) {
        return baseUpload.delete(dto.getKey());
    }

    /**
     * 下载文件
     * (BaseUpload 暂未定义 download 方法，通常 OSS SDK 提供流下载)
     * (此处需要子类实现具体的流获取逻辑，或扩展 BaseUpload)
     * 
     * @param dto 文件键 dto
     * @return 文件字节数组
     */
    @Override
    public abstract byte[] download(FileKeyDto dto);

    /**
     * 获取文件 URL
     *
     * @param dto 文件键 dto
     * @return 文件 URL
     */
    @Override
    public final String getUrl(FileKeyDto dto) {
        return baseUpload.getUrl(dto.getKey());
    }

    /**
     * 检查文件是否存在
     *
     * @param dto 文件键 dto
     * @return 是否存在
     */
    @Override
    public final boolean exists(FileKeyDto dto) {
        return baseUpload.exists(dto.getKey());
    }
}
