package com.muou.usercenter.common;

/**
 * 错误码返回
 *
 * @author muou
 */
public enum ErrorCodeEnum {

    SUCCESS(200,"操作成功", ""),
    SYSTEM_ERROR(1000,"服务器内部错误", ""),
    PARAMS_ERROR(2001, "请求参数错误", ""),
    NULL_ERROR(2002, "请求数据为空", ""),
    NO_LOGIN(2003, "用户未登录", ""),
    NO_AUTH(2004, "用户无权限", ""),
    USER_IS_EXIST(2005, "用户已存在", ""),
    USER_NOT_EXIST(2006, "用户不存在", "");



    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态码信息
     */
    private final String message;

    /**
     * 状态码描述（详情）
     */
    private final String description;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

    ErrorCodeEnum(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }
}
