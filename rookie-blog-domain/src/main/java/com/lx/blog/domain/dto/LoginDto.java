package com.lx.blog.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author 李旭
 * @date 2025/11/14
 * @description 登录请求参数
 */
@Data
public class LoginDto {

    @NotBlank(message = "{login.username.notBlank}")
    @Schema(description = "用户名", example = "admin")
    private String username;

    @NotBlank(message = "{login.password.notBlank}")
    @Schema(description = "密码", example = "123456")
    private String password;

    @NotBlank(message = "{login.ipAddress.notBlank}")
    @Schema(description = "登录IP地址", example = "127.0.0.1")
    private String ipAddress;

}
