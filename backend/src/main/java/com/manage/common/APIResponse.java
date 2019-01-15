package com.manage.common;

import com.manage.exception.ResponseStatusEnum;
import com.manage.exception.impl.SuccessStatusEnum;

public class APIResponse {

    Integer code;
    String message;

    public static class SuccessAPIResponse extends APIResponse{
        Object content;

        public Object getContent() {
            return content;
        }

        public void setContent(Object content) {
            this.content = content;
        }
    }

    public static APIResponse toOkResponse() {
        APIResponse response = new APIResponse();
        response.setCode(SuccessStatusEnum.OK.getCode());
        response.setMessage(SuccessStatusEnum.OK.getMessage());
        return response;
    }

    public static APIResponse toOkResponse(Object data) {
        return toOkResponse(data,SuccessStatusEnum.OK.getMessage());
    }

    public static APIResponse toOkResponse(Object data,String message) {
        SuccessAPIResponse response = new SuccessAPIResponse();
        response.setContent(data);
        response.setMessage(message);
        response.setCode(SuccessStatusEnum.OK.getCode());
        return response;
    }

    public static APIResponse toExceptionResponse(ResponseStatusEnum exceptionEnum){
        APIResponse response = new APIResponse();
        response.setCode(exceptionEnum.getCode());
        response.setMessage(exceptionEnum.getMessage());
        return response;
    }

    public static APIResponse toExceptionResponse(Integer code,String message){
        APIResponse response = new APIResponse();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}