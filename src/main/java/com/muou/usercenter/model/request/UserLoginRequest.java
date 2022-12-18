package com.muou.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author mccc
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 5750647893781673779L;

    private String userAccount;

    private String userPassword;
}
