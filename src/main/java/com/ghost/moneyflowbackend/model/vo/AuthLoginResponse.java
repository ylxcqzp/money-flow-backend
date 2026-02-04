package com.ghost.moneyflowbackend.model.vo;

import lombok.Data;

/**
 * 登录/注册响应对象
 */
@Data
public class AuthLoginResponse {

    /**
     * JWT Token
     */
    private String token;

    /**
     * 用户信息
     */
    private AuthUserVO user;
}
