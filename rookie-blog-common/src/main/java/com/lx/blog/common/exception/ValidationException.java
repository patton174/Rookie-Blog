package com.lx.blog.common.exception;

import com.lx.blog.common.base.BaseException;
import com.lx.blog.common.base.ResultCode;

/**
 * @author LX
 * @description 参数校验异常
 */
public class ValidationException extends BaseException {

    /**
     * 构造函数
     */
    public ValidationException() {
        super(ResultCode.BAD_REQUEST);
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     */
    public ValidationException(String message) {
        super(ResultCode.BAD_REQUEST, message);
    }

    /**
     * 构造函数
     *
     * @param cause 异常原因
     */
    public ValidationException(Throwable cause) {
        super(ResultCode.BAD_REQUEST, cause);
    }

    /**
     * 构造函数
     *
     * @param message 异常消息
     * @param cause   异常原因
     */
    public ValidationException(String message, Throwable cause) {
        super(ResultCode.BAD_REQUEST, message, cause);
    }
}
