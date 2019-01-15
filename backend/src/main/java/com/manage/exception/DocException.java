package com.manage.exception;

public class DocException extends RuntimeException {

    private int code;

    private String message;

    public DocException(ResponseStatusEnum responseStatusEnum) {
        this.code = responseStatusEnum.getCode();
        this.message = responseStatusEnum.getMessage();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
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