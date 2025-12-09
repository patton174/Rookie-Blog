package com.lx.blog.common.exception;

import com.lx.blog.common.base.BaseException;
import com.lx.blog.common.base.ResultCode;

/**
 * @author LX
 * @date 2025/12/9
 * @description 系统错误异常
 */
public class SystemErrorException extends BaseException {

    /**
     * 构造函数
     */
    public SystemErrorException() {
        super(ResultCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     */
    public SystemErrorException(String message) {
        super(ResultCode.INTERNAL_SERVER_ERROR, message);
    }

    /**
     * 构造函数
     *
     * @param cause 异常原因
     */
    public SystemErrorException(Throwable cause) {
        super(ResultCode.INTERNAL_SERVER_ERROR, cause);
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     * @param cause   异常原因
     */
    public SystemErrorException(String message, Throwable cause) {
        super(ResultCode.INTERNAL_SERVER_ERROR, message, cause);
    }
}
