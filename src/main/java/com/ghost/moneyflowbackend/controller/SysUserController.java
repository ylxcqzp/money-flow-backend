package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.service.SysUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/sys-users")
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 构造方法
     *
     * @param sysUserService 用户业务服务
     */
    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }
}
