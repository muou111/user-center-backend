package com.muou.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author mccc
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -8860241916063270207L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
}
