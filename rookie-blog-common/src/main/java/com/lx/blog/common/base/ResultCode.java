package com.lx.blog.common.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author LX
 * @date 2025/11/12
 * @description 错误码枚举（支持国际化键）
 */
@AllArgsConstructor
public enum ResultCode implements Serializable {

    SUCCESS(200, "成功", "common.success"),
    FAIL(500, "失败", "common.fail"),
    UNAUTHORIZED(401, "未认证", "error.unauthorized"),
    FORBIDDEN(403, "未授权", "error.forbidden"),
    NOT_FOUND(404, "资源不存在", "error.not_found"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误", "error.internal"),
    BAD_REQUEST(400, "请求参数错误", "error.bad_request"),
    CONFLICT(409, "资源冲突", "error.conflict"),
    TOO_MANY_REQUESTS(429, "请求频率过快", "error.too_many_requests"),
    UNKNOWN_ERROR(500, "未知错误", "error.unknown"),
    CUSTOM_ERROR(500, "自定义错误", "error.custom");

    private static final long serialVersionUID = 100000000001L;

    @Getter private final Integer errCode;
    @Getter private final String errMsg;
    @Getter private final String messageKey;
}
