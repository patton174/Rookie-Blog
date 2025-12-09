package com.lx.blog.domain.vo;

import lombok.Data;

/**
 * @author LX
 * @date 2025/12/8
 * @description 文件 vo
 */
@Data
public class FileVo {
    private String key;
    private String url;
    private String contentType;
    private Long size;
}
