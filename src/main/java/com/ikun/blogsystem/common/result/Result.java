package com.ikun.blogsystem.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一接口响应包装类
 * @param <T> 响应数据泛型
 */
@Data
public class Result<T> implements Serializable {
    private int code;
    private String msg;
    private T data;

    // 私有化构造器，强制通过静态方法创建
    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功返回 - 无数据
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
    }

    /**
     * 成功返回 - 有数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    /**
     * 成功返回 - 自定义提示信息
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    /**
     * 失败返回 - 默认业务异常
     */
    public static <T> Result<T> error() {
        return new Result<>(ResultCode.FAILURE.getCode(), ResultCode.FAILURE.getMsg(), null);
    }

    /**
     * 失败返回 - 自定义错误码和信息
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMsg(), null);
    }

    /**
     * 失败返回 - 完全自定义信息
     */
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }
}