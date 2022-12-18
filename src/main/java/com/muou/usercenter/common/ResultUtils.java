package com.muou.usercenter.common;

/**
 * 返回工具类
 *
 * @author muou
 */
public class ResultUtils {

    /**
     *  成功返回
     * @param data 返回数据
     * @return 通用返回类
     * @param <T> 泛型
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200,"Success", data);
    }

    /**
     * 错误返回
     * @param error
     * @return
     */
    public static BaseResponse error(ErrorCodeEnum error) {
        return new BaseResponse(error);
    }

    public static BaseResponse error(ErrorCodeEnum error, String message, String description) {
        return new BaseResponse(error.getCode(), message, description,"");
    }

    /**
     * 错误返回
     *
     * @param code
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error(int code, String message, String description) {
        return new BaseResponse(code, message, description,"");
    }
}
