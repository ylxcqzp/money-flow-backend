package com.ghost.moneyflowbackend.model.vo;

import lombok.Data;

/**
 * 登录用户信息返回对象
 */
@Data
public class AuthUserVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像地址
     */
    private String avatarUrl;
}
