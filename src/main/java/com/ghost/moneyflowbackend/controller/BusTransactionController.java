package com.ghost.moneyflowbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.entity.BusTransaction;
import com.ghost.moneyflowbackend.service.BusTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 交易明细接口
 */
@Tag(name = "交易明细管理")
@Validated
@RestController
@RequestMapping("/api/transactions")
public class BusTransactionController {

    private final BusTransactionService busTransactionService;

    /**
     * 交易明细接口构造方法
     *
     * @param busTransactionService 交易业务服务
     */
    public BusTransactionController(BusTransactionService busTransactionService) {
        this.busTransactionService = busTransactionService;
    }

    /**
     * 创建交易
     *
     * @param tx 交易信息
     * @return 创建结果
     */
    @Operation(summary = "创建交易")
    @PostMapping
    public Result<BusTransaction> create(@Valid @RequestBody BusTransaction tx) {
        busTransactionService.save(tx);
        return Result.ok(tx);
    }

    /**
     * 更新交易
     *
     * @param id 交易ID
     * @param tx 交易信息
     * @return 更新结果
     */
    @Operation(summary = "更新交易")
    @PutMapping("/{id}")
    public Result<BusTransaction> update(@PathVariable @NotNull Long id, @Valid @RequestBody BusTransaction tx) {
        tx.setId(id);
        busTransactionService.updateById(tx);
        return Result.ok(tx);
    }

    /**
     * 根据ID查询交易
     *
     * @param id 交易ID
     * @return 交易信息
     */
    @Operation(summary = "根据ID查询交易")
    @GetMapping("/{id}")
    public Result<BusTransaction> getById(@PathVariable @NotNull Long id) {
        return Result.ok(busTransactionService.getById(id));
    }

    /**
     * 分页查询交易
     *
     * @param page 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询交易")
    @GetMapping("/page")
    public Result<IPage<BusTransaction>> page(@RequestParam(defaultValue = "1") @Min(1) long page,
                                              @RequestParam(defaultValue = "10") @Min(1) long size) {
        return Result.ok(busTransactionService.page(new Page<>(page, size)));
    }

    /**
     * 删除交易
     *
     * @param id 交易ID
     * @return 删除结果
     */
    @Operation(summary = "删除交易")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.ok(busTransactionService.removeById(id));
    }
}
