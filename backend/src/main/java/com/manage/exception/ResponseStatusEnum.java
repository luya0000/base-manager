package com.manage.exception;

/**
 * 所有异常信息编码父类
 */
public interface ResponseStatusEnum {

    /**
     * 获取异常编码
     */
    Integer getCode();

    /**
     * 获取异常信息
     */
    String getMessage();
}