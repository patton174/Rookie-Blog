package com.lx.blog.common.exception;

import com.lx.blog.common.base.BaseException;
import com.lx.blog.common.base.ResultCode;

/**
 * @author LX
 * @date 2025/11/13
 * @description 禁止访问(未授权)异常
 */
public class ForbiddenException extends BaseException {

    /**
     * 构造函数
     */
    public ForbiddenException() {
        super(ResultCode.FORBIDDEN);
    }

    /**
     * 构造函数
     * @param message 异常信息
     */
    public ForbiddenException(String message) {
        super(ResultCode.FORBIDDEN, message);
    }

    /**
     * 构造函数
     * @param resultCode 错误码
     */
    public ForbiddenException(ResultCode resultCode) {
        super(resultCode);
    }

    /**
     * 构造函数
     * @param resultCode 错误码
     * @param message 异常信息
     */
    public ForbiddenException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    /**
     * 构造函数
     * @param message 异常信息
     * @param cause 异常原因
     */
    public ForbiddenException(String message, Throwable cause) {
        super(ResultCode.FORBIDDEN, message, cause);
    }
}
