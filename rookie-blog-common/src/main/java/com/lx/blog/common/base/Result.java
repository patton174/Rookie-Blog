package com.lx.blog.common.base;

import com.lx.blog.common.utils.I18nUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LX
 * @date 2025/11/13
 * @description 通用返回结果封装
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /**
     * 错误码
     */
    private Integer errCode;

    /**
     * 错误信息
     */
    private String errMsg;

    /**
     * 是否成功
     */
    private Boolean isSuccess;

     /**
      * 数据
      */
    private T data;


    public Result(Integer errCode, String errMsg, Boolean isSuccess) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.isSuccess = isSuccess;
    }

    /**
     * 成功返回结果
     * @param <T> 数据类型
     * @return 成功返回结果
     */
    public static <T> Result<T> ok() {
        return new Result<>(ResultCode.SUCCESS.getErrCode(), I18nUtils.from(ResultCode.SUCCESS), Boolean.TRUE);
    }

    /**
     * 成功返回结果
     * @param errCode 错误码
     * @return 成功返回结果
     * @param <T> 数据类型
     */
    public static <T> Result<T> ok(Integer errCode) {
        return new Result<>(errCode, I18nUtils.from(ResultCode.SUCCESS), Boolean.TRUE);
    }

     /**
      * 成功返回结果
      * @param errMsg 错误信息
      * @return 成功返回结果
      * @param <T> 数据类型
      */
    public static <T> Result<T> ok(String errMsg) {
        return new Result<>(ResultCode.SUCCESS.getErrCode(), errMsg, Boolean.TRUE);
    }

    /**
     * 成功返回结果
     * @param errCode 错误码
     * @param errMsg 错误信息
     * @return 成功返回结果
     * @param <T> 数据类型
     */
    public static <T> Result<T> ok(Integer errCode, String errMsg) {
        return new Result<>(errCode, errMsg, Boolean.TRUE);
    }

    /**
     * 成功返回结果
     * @param code 错误码枚举
     * @return 成功返回结果
     * @param <T> 数据类型
     */
    public static <T> Result<T> ok(ResultCode code) {
        return new Result<>(code.getErrCode(), I18nUtils.from(code), Boolean.TRUE);
    }

     /**
      * 成功返回结果
      * @param data 数据
      * @return 成功返回结果
      * @param <T> 数据类型
      */
    public static <T> Result<T> ok(T data) {
        return new Result<>(ResultCode.SUCCESS.getErrCode(), I18nUtils.from(ResultCode.SUCCESS), Boolean.TRUE, data);
    }

    /**
     * 成功返回结果
     * @param data 数据
     * @param errMsg 错误信息
     * @return 成功返回结果
     * @param <T> 数据类型
     */
    public static <T> Result<T> ok(T data, String errMsg) {
        return new Result<>(ResultCode.SUCCESS.getErrCode(), errMsg, Boolean.TRUE, data);
    }

    /**
     * 失败返回结果
     * @return 失败返回结果
     * @param <T> 数据类型
     */
    public static <T> Result<T> error() {
        return new Result<>(ResultCode.FAIL.getErrCode(), I18nUtils.from(ResultCode.FAIL), Boolean.FALSE);
    }

    /**
     * 失败返回结果
     * @param errMsg 错误信息
     * @return 失败返回结果
     * @param <T> 数据类型
     */
    public static <T> Result<T> error(String errMsg) {
        return new Result<>(ResultCode.FAIL.getErrCode(), errMsg, Boolean.FALSE);
    }

    /**
     * 失败返回结果
     * @param errCode 错误码
     * @param errMsg 错误信息
     * @return 失败返回结果
     * @param <T> 数据类型
     */
    public static <T> Result<T> error(Integer errCode, String errMsg) {
        return new Result<>(errCode, errMsg, Boolean.FALSE);
    }

    /**
     * 失败返回结果
     * @param code 错误码枚举
     * @return 失败返回结果
     * @param <T> 数据类型
     */
    public static <T> Result<T> error(ResultCode code) {
        return new Result<>(code.getErrCode(), I18nUtils.from(code), Boolean.FALSE);
    }

    /**
     * 失败返回结果
     * @param errors 错误数据
     * @return 失败返回结果
     * @param <T> 数据类型
     */
    public static <T> Result<T> error(T errors) {
        return new Result<>(ResultCode.FAIL.getErrCode(), I18nUtils.from(ResultCode.FAIL), Boolean.FALSE, errors);
    }

}
