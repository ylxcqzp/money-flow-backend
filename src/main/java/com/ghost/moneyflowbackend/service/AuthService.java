package com.ghost.moneyflowbackend.service;

import com.ghost.moneyflowbackend.common.security.SysUserDetails;
import com.ghost.moneyflowbackend.model.dto.AuthLoginRequest;
import com.ghost.moneyflowbackend.model.dto.AuthRegisterRequest;
import com.ghost.moneyflowbackend.model.vo.AuthLoginResponse;
import com.ghost.moneyflowbackend.model.vo.AuthMeResponse;

/**
 * 登录注册鉴权服务
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param request 登录参数
     * @return 登录结果
     */
    AuthLoginResponse login(AuthLoginRequest request);

    /**
     * 用户注册
     *
     * @param request 注册参数
     * @return 注册结果
     */
    AuthLoginResponse register(AuthRegisterRequest request);

    /**
     * 获取当前用户信息
     *
     * @param userDetails 认证用户详情
     * @return 当前用户信息
     */
    AuthMeResponse currentUser(SysUserDetails userDetails);
}
