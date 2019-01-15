package com.manage.exception.impl;


import com.manage.exception.ResponseStatusEnum;

/**
 * @author luya
 * @created 2018-05-29
 */
public enum SuccessStatusEnum implements ResponseStatusEnum {

    OK(200, "OK"),
    ERROR(401, "NG");
    ;

    SuccessStatusEnum(int code, String message) {
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
