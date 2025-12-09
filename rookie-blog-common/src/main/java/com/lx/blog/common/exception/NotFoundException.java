package com.lx.blog.common.exception;

import com.lx.blog.common.base.BaseException;
import com.lx.blog.common.base.ResultCode;

/**
 * @author LX
 * @date 2025/11/13
 * @description 资源未找到异常
 */
public class NotFoundException extends BaseException {

    /**
     * 构造函数
     */
    public NotFoundException() {
        super(ResultCode.NOT_FOUND);
    }

    /**
     * 构造函数
     * @param resultCode 错误码
     */
    public NotFoundException(ResultCode resultCode) {
        super(resultCode);
    }

    /**
     * 构造函数
     * @param message 异常信息
     */
    public NotFoundException(String message) {
        super(ResultCode.NOT_FOUND, message);
    }

    /**
     * 构造函数
     * @param errcode 错误码
     * @param message 异常信息
     */
    public NotFoundException(Integer errcode, String message) {
        super(errcode, message);
    }

     /**
      * 构造函数
      * @param errcode 错误码
      * @param message 异常信息
      * @param cause 异常原因
      */
    public NotFoundException(Integer errcode, String message, Throwable cause) {
        super(errcode, message, cause);
    }
}
