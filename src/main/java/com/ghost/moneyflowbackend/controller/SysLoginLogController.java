package com.ghost.moneyflowbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.entity.SysLoginLog;
import com.ghost.moneyflowbackend.service.SysLoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 登录日志接口
 */
@Tag(name = "登录日志管理")
@Validated
@RestController
@RequestMapping("/api/login-logs")
public class SysLoginLogController {

    private final SysLoginLogService sysLoginLogService;

    /**
     * 登录日志接口构造方法
     *
     * @param sysLoginLogService 登录日志业务服务
     */
    public SysLoginLogController(SysLoginLogService sysLoginLogService) {
        this.sysLoginLogService = sysLoginLogService;
    }

    /**
     * 创建登录日志
     *
     * @param log 登录日志
     * @return 创建结果
     */
    @Operation(summary = "创建登录日志")
    @PostMapping
    public Result<SysLoginLog> create(@Valid @RequestBody SysLoginLog log) {
        sysLoginLogService.save(log);
        return Result.ok(log);
    }

    /**
     * 更新登录日志
     *
     * @param id 日志ID
     * @param log 登录日志
     * @return 更新结果
     */
    @Operation(summary = "更新登录日志")
    @PutMapping("/{id}")
    public Result<SysLoginLog> update(@PathVariable @NotNull Long id, @Valid @RequestBody SysLoginLog log) {
        log.setId(id);
        sysLoginLogService.updateById(log);
        return Result.ok(log);
    }

    /**
     * 根据ID查询登录日志
     *
     * @param id 日志ID
     * @return 登录日志
     */
    @Operation(summary = "根据ID查询登录日志")
    @GetMapping("/{id}")
    public Result<SysLoginLog> getById(@PathVariable @NotNull Long id) {
        return Result.ok(sysLoginLogService.getById(id));
    }

    /**
     * 分页查询登录日志
     *
     * @param page 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询登录日志")
    @GetMapping("/page")
    public Result<IPage<SysLoginLog>> page(@RequestParam(defaultValue = "1") @Min(1) long page,
                                           @RequestParam(defaultValue = "10") @Min(1) long size) {
        return Result.ok(sysLoginLogService.page(new Page<>(page, size)));
    }

    /**
     * 删除登录日志
     *
     * @param id 日志ID
     * @return 删除结果
     */
    @Operation(summary = "删除登录日志")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.ok(sysLoginLogService.removeById(id));
    }
}
