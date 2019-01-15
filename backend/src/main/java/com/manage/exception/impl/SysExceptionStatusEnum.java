package com.manage.exception.impl;

import com.manage.exception.ResponseStatusEnum;

/**
 * 核心异常枚举
 *
 * @author luya
 */
public enum SysExceptionStatusEnum implements ResponseStatusEnum {

    /**
     * 错误的请求
     */
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "没有授权"),
    FORBIDDEN(403, "禁止"),
    NOT_FOUND(404, "资源未找到"),
    METHOD_NOT_ALLOWED(405, "不允许该Method方式请求"),
    SERVER_ERROR(500, "服务器异常"),
    /**
     * 其他
     */
    WRITE_ERROR(501, "渲染界面错误"),
    PARAMETER_VALIDATE_ERROR(502, "参数校验错误"),

    /**
     * token异常
     */
    TOKEN_EXPIRED(700, "token过期"),
    TOKEN_ERROR(701, "token验证失败"),
    SIGN_ERROR(702, "签名验证失败"),;

    SysExceptionStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}