package com.ghost.moneyflowbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.entity.BusAccount;
import com.ghost.moneyflowbackend.service.BusAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 资产账户接口
 */
@Tag(name = "资产账户管理")
@Validated
@RestController
@RequestMapping("/api/accounts")
public class BusAccountController {

    private final BusAccountService busAccountService;

    /**
     * 资产账户接口构造方法
     *
     * @param busAccountService 资产账户业务服务
     */
    public BusAccountController(BusAccountService busAccountService) {
        this.busAccountService = busAccountService;
    }

    /**
     * 创建账户
     *
     * @param account 账户信息
     * @return 创建结果
     */
    @Operation(summary = "创建账户")
    @PostMapping
    public Result<BusAccount> create(@Valid @RequestBody BusAccount account) {
        busAccountService.save(account);
        return Result.ok(account);
    }

    /**
     * 更新账户
     *
     * @param id 账户ID
     * @param account 账户信息
     * @return 更新结果
     */
    @Operation(summary = "更新账户")
    @PutMapping("/{id}")
    public Result<BusAccount> update(@PathVariable @NotNull Long id, @Valid @RequestBody BusAccount account) {
        account.setId(id);
        busAccountService.updateById(account);
        return Result.ok(account);
    }

    /**
     * 根据ID查询账户
     *
     * @param id 账户ID
     * @return 账户信息
     */
    @Operation(summary = "根据ID查询账户")
    @GetMapping("/{id}")
    public Result<BusAccount> getById(@PathVariable @NotNull Long id) {
        return Result.ok(busAccountService.getById(id));
    }

    /**
     * 分页查询账户
     *
     * @param page 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询账户")
    @GetMapping("/page")
    public Result<IPage<BusAccount>> page(@RequestParam(defaultValue = "1") @Min(1) long page,
                                          @RequestParam(defaultValue = "10") @Min(1) long size) {
        return Result.ok(busAccountService.page(new Page<>(page, size)));
    }

    /**
     * 删除账户
     *
     * @param id 账户ID
     * @return 删除结果
     */
    @Operation(summary = "删除账户")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.ok(busAccountService.removeById(id));
    }
}
