package com.ghost.moneyflowbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.entity.SysUserDevice;
import com.ghost.moneyflowbackend.service.SysUserDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户设备接口
 */
@Tag(name = "用户设备管理")
@Validated
@RestController
@RequestMapping("/api/user-devices")
public class SysUserDeviceController {

    private final SysUserDeviceService sysUserDeviceService;

    /**
     * 用户设备接口构造方法
     *
     * @param sysUserDeviceService 用户设备业务服务
     */
    public SysUserDeviceController(SysUserDeviceService sysUserDeviceService) {
        this.sysUserDeviceService = sysUserDeviceService;
    }

    /**
     * 绑定用户设备
     *
     * @param device 设备信息
     * @return 创建结果
     */
    @Operation(summary = "绑定用户设备")
    @PostMapping
    public Result<SysUserDevice> create(@Valid @RequestBody SysUserDevice device) {
        sysUserDeviceService.save(device);
        return Result.ok(device);
    }

    /**
     * 更新用户设备
     *
     * @param id 设备记录ID
     * @param device 设备信息
     * @return 更新结果
     */
    @Operation(summary = "更新用户设备")
    @PutMapping("/{id}")
    public Result<SysUserDevice> update(@PathVariable @NotNull Long id, @Valid @RequestBody SysUserDevice device) {
        device.setId(id);
        sysUserDeviceService.updateById(device);
        return Result.ok(device);
    }

    /**
     * 根据ID查询用户设备
     *
     * @param id 设备记录ID
     * @return 设备信息
     */
    @Operation(summary = "根据ID查询用户设备")
    @GetMapping("/{id}")
    public Result<SysUserDevice> getById(@PathVariable @NotNull Long id) {
        return Result.ok(sysUserDeviceService.getById(id));
    }

    /**
     * 分页查询用户设备
     *
     * @param page 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询用户设备")
    @GetMapping("/page")
    public Result<IPage<SysUserDevice>> page(@RequestParam(defaultValue = "1") @Min(1) long page,
                                             @RequestParam(defaultValue = "10") @Min(1) long size) {
        return Result.ok(sysUserDeviceService.page(new Page<>(page, size)));
    }

    /**
     * 删除用户设备
     *
     * @param id 设备记录ID
     * @return 删除结果
     */
    @Operation(summary = "删除用户设备")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.ok(sysUserDeviceService.removeById(id));
    }
}
