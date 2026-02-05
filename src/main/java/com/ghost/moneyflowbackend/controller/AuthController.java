package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.model.dto.AuthLoginRequest;
import com.ghost.moneyflowbackend.model.dto.AuthRegisterRequest;
import com.ghost.moneyflowbackend.model.vo.AuthLoginResponse;
import com.ghost.moneyflowbackend.model.vo.AuthMeResponse;
import com.ghost.moneyflowbackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理")
@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * 认证服务
     */
    private final AuthService authService;

    /**
     * 用户登录
     *
     * @param request 登录参数
     * @return 登录结果
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        return Result.ok(authService.login(request));
    }

    /**
     * 用户注册
     *
     * @param request 注册参数
     * @return 注册结果
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<AuthLoginResponse> register(@Valid @RequestBody AuthRegisterRequest request) {
        return Result.ok(authService.register(request));
    }

    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<AuthMeResponse> me() {
        return Result.ok(authService.currentUser());
    }

    /**
     * 退出登录
     *
     * @return 退出登录结果
     */
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok(null);
    }
}
