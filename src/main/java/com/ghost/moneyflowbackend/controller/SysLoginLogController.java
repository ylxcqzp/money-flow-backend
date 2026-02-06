package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.service.SysLoginLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "登录日志管理")
@RestController
@RequestMapping("/api/login-logs")
public class SysLoginLogController {

    private final SysLoginLogService sysLoginLogService;

    /**
     * 构造方法
     *
     * @param sysLoginLogService 登录日志业务服务
     */
    public SysLoginLogController(SysLoginLogService sysLoginLogService) {
        this.sysLoginLogService = sysLoginLogService;
    }
}
