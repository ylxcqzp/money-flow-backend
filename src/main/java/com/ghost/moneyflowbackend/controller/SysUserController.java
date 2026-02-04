package com.ghost.moneyflowbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.entity.SysUser;
import com.ghost.moneyflowbackend.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口
 */
@Tag(name = "用户管理")
@Validated
@RestController
@RequestMapping("/api/sys-users")
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 用户接口构造方法
     *
     * @param sysUserService 用户业务服务
     */
    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    /**
     * 创建用户
     *
     * @param user 用户信息
     * @return 创建结果
     */
    @Operation(summary = "创建用户")
    @PostMapping
    public Result<SysUser> create(@Valid @RequestBody SysUser user) {
        sysUserService.save(user);
        return Result.ok(user);
    }

    /**
     * 更新用户
     *
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<SysUser> update(@PathVariable @NotNull Long id, @Valid @RequestBody SysUser user) {
        user.setId(id);
        sysUserService.updateById(user);
        return Result.ok(user);
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable @NotNull Long id) {
        return Result.ok(sysUserService.getById(id));
    }

    /**
     * 分页查询用户
     *
     * @param page 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<IPage<SysUser>> page(@RequestParam(defaultValue = "1") @Min(1) long page,
                                       @RequestParam(defaultValue = "10") @Min(1) long size) {
        return Result.ok(sysUserService.page(new Page<>(page, size)));
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.ok(sysUserService.removeById(id));
    }
}
