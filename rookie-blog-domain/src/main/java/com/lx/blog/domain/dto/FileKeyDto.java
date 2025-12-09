package com.lx.blog.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author LX
 * @date 2025/12/8
 * @description 文件键 dto
 */
@Data
public class FileKeyDto {
    @NotBlank(message = "对象键不能为空")
    private String key;
}
