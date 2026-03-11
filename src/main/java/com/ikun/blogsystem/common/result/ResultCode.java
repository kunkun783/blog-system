package com.ikun.blogsystem.common.result;

import lombok.Getter;

/**
 * 业务状态码枚举
 */
@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    FAILURE(500, "业务异常"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "没有相关权限"),
    PARAM_ERROR(400, "参数校验失败"),
    NOT_FOUND(404, "资源不存在"),
    USER_NOT_FOUND(1001, "用户不存在"),
    PASSWORD_ERROR(1002, "密码错误"),
    BLOG_NOT_AUDITED(2001, "博文待审核或审核未通过");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
