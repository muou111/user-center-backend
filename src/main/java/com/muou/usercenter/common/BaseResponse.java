package com.muou.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通过返回类
 * @param <T>
 * @author muou
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private String message;

    private String description;

    private T data;

    public BaseResponse(int code, String message, String description, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.description = description;
    }

    public BaseResponse(int code, String message, T data) {
        this(code, message, "", data);
    }

    public BaseResponse(ErrorCodeEnum error) {
        this(error.getCode(), error.getMessage(), null);
    }
}
