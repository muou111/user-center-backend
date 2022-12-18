package com.muou.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询请求体
 *
 * @author mccc
 */
@Data
public class UserSearchRequest implements Serializable {
    private static final long serialVersionUID = 3521991379166922598L;

    private String username;
}
