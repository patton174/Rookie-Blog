package com.lx.blog.service;

import com.lx.blog.domain.dto.FileKeyDto;
import com.lx.blog.domain.dto.FileUploadDto;
import com.lx.blog.domain.vo.FileVo;

/**
 * @author LX
 * @date 2025/12/8
 * @description 文件服务接口
 */
public interface FileService {

    /**
     * 上传文件
     *
     * @param dto 文件上传 dto
     * @return 文件 vo
     */
    FileVo upload(FileUploadDto dto);

    /**
     * 删除文件
     *
     * @param dto 文件键 dto
     * @return 是否删除成功
     */
    boolean delete(FileKeyDto dto);

    /**
     * 下载文件
     *
     * @param dto 文件键 dto
     * @return 文件字节数组
     */
    byte[] download(FileKeyDto dto);

    /**
     * 获取文件 URL
     *
     * @param dto 文件键 dto
     * @return 文件 URL
     */
    String getUrl(FileKeyDto dto);

    /**
     * 检查文件是否存在
     *
     * @param dto 文件键 dto
     * @return 是否存在
     */
    boolean exists(FileKeyDto dto);
}
