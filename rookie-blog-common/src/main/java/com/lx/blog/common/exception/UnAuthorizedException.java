package com.lx.blog.common.exception;

import com.lx.blog.common.base.BaseException;
import com.lx.blog.common.base.ResultCode;

/**
 * @author LX
 * @date 2025/11/13
 * @description 未认证异常
 */
public class UnAuthorizedException extends BaseException {

    /**
     * 构造函数
     */
    public UnAuthorizedException() {
        super(ResultCode.UNAUTHORIZED);
    }

    /**
     * 构造函数
     * @param resultCode 错误码
     */
    public UnAuthorizedException(ResultCode resultCode) {
        super(resultCode);
    }

    /**
     * 构造函数
     * @param message 异常信息
     */
    public UnAuthorizedException(String message) {
        super(ResultCode.UNAUTHORIZED, message);
    }

    /**
     * 构造函数
     * @param message 异常信息
     */
    public UnAuthorizedException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    /**
     * 构造函数
     * @param message 异常信息
     * @param cause 异常原因
     */
    public UnAuthorizedException(ResultCode resultCode, String message, Throwable cause) {
        super(resultCode, message, cause);
    }

}
