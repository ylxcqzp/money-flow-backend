package com.ghost.moneyflowbackend.model.vo;

import lombok.Data;

/**
 * 当前用户信息响应对象
 */
@Data
public class AuthMeResponse {

    /**
     * 用户信息
     */
    private AuthUserVO user;
}
