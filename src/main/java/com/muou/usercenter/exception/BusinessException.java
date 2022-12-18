package com.muou.usercenter.exception;

import com.muou.usercenter.common.ErrorCodeEnum;

/**
 * 自定义异常类
 */
public class BusinessException extends RuntimeException {

    private final int code;

    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getMessage());
        this.code = errorCodeEnum.getCode();
        this.description = errorCodeEnum.getDescription();
    }

    public BusinessException(ErrorCodeEnum errorCodeEnum, String description) {
        super(errorCodeEnum.getMessage());
        this.code = errorCodeEnum.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
