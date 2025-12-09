package com.lx.blog.common.service;

import cn.dev33.satoken.exception.NotLoginException;
import com.lx.blog.common.base.ResultCode;
import com.lx.blog.common.base.BaseException;
import com.lx.blog.common.exception.ForbiddenException;
import com.lx.blog.common.exception.NotFoundException;
import com.lx.blog.common.exception.SystemErrorException;
import com.lx.blog.common.exception.UnAuthorizedException;
import com.lx.blog.common.exception.ValidationException;
import com.lx.blog.common.base.Result;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ConstraintViolation;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author LX
 * @date 2025/11/13
 * @description 全局异常捕获
 */
@Slf4j
@RestControllerAdvice
public class HandlerException extends BaseBiz {

    public HandlerException() {}

    /**
     * 捕获基础异常
     * @param e 基础异常
     * @return 异常信息
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Result<Object>> handleBaseException(BaseException e) {
        log.error("handleBaseException: {}", e.getMessage(),e);
        return ResponseEntity
                .status(e.getErrCode())
                .body(Result.error(e.getErrCode(), e.getMessage()));
    }

    /**
     * 捕获资源未找到异常
     * @param e 资源未找到异常
     * @return 异常信息
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Result<Object>> handleNotFoundException(NotFoundException e) {
        log.error("handleNotFoundException: {}", e.getMessage(),e);
        return ResponseEntity
                .status(e.getErrCode())
                .body(Result.error(e.getErrCode(), e.getMessage()));
    }

    /**
     * 捕获未授权异常
     * @param e 未认证异常
     * @return 异常信息
     */
    @ExceptionHandler(UnAuthorizedException.class)
    public ResponseEntity<Result<Object>> handleUnAuthorizedException(UnAuthorizedException e) {
        log.error("handleUnAuthorizedException: {}", e.getMessage(),e);
        return ResponseEntity
                .status(e.getErrCode())
                .body(Result.error(e.getErrCode(), e.getMessage()));
    }

    /**
     * 捕获禁止访问异常
     * @param e 禁止访问异常
     * @return 异常信息
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Result<Object>> handleForbiddenException(ForbiddenException e) {
        log.error("handleForbiddenException: {}", e.getMessage(),e);
        return ResponseEntity
                .status(e.getErrCode())
                .body(Result.error(e.getErrCode(), e.getMessage()));
    }

    /**
     * 捕获未登录异常
     * @param e 未登录异常
     * @return 异常信息
     */
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<Result<Object>> handleNotLoginException(NotLoginException e) {
        log.error("handleNotLoginException: {}", e.getMessage());
        return ResponseEntity
                .status(ResultCode.UNAUTHORIZED.getErrCode())
                .body(Result.error(ResultCode.UNAUTHORIZED.getErrCode(), I18n(ResultCode.UNAUTHORIZED)));
    }

    /**
     * Validation参数校验错误异常
     * @param e 参数校验错误异常
     * @return 异常信息
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<Object>> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("handleConstraintViolationException: {}", e.getMessage(), e);
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(ResultCode.BAD_REQUEST.getErrCode())
                .body(Result.error(ResultCode.BAD_REQUEST.getErrCode(), msg));
    }

    /**
     * 捕获自定义参数校验异常
     * @param e 参数校验异常
     * @return 异常信息
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Result<Object>> handleValidationException(ValidationException e) {
        log.error("handleValidationException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(e.getErrCode())
                .body(Result.error(e.getErrCode(), e.getMessage()));
    }

    /**
     * 捕获系统错误异常
     * @param e 系统错误异常
     * @return 异常信息
     */
    @ExceptionHandler(SystemErrorException.class)
    public ResponseEntity<Result<Object>> handleSystemErrorException(SystemErrorException e) {
        log.error("handleSystemErrorException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(e.getErrCode())
                .body(Result.error(e.getErrCode(), e.getMessage()));
    }

    /**
     * 捕获所有未处理的异常 (兜底)
     * @param e 异常
     * @return 异常信息
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Object>> handleGlobalException(Exception e) {
        log.error("handleGlobalException: {}", e.getMessage(), e);
        return ResponseEntity
                .status(ResultCode.INTERNAL_SERVER_ERROR.getErrCode())
                .body(Result.error(ResultCode.INTERNAL_SERVER_ERROR.getErrCode(), I18n(ResultCode.INTERNAL_SERVER_ERROR)));
    }
}
