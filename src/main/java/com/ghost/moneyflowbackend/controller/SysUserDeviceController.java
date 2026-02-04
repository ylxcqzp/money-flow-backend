package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.service.SysUserDeviceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户设备管理")
@RestController
@RequestMapping("/api/user-devices")
public class SysUserDeviceController {

    private final SysUserDeviceService sysUserDeviceService;

    public SysUserDeviceController(SysUserDeviceService sysUserDeviceService) {
        this.sysUserDeviceService = sysUserDeviceService;
    }
}
