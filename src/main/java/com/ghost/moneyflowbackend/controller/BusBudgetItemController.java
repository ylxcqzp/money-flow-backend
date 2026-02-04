package com.ghost.moneyflowbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.entity.BusBudgetItem;
import com.ghost.moneyflowbackend.service.BusBudgetItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "预算明细管理")
@Validated
@RestController
@RequestMapping("/api/budget-items")
public class BusBudgetItemController {

    private final BusBudgetItemService busBudgetItemService;

    public BusBudgetItemController(BusBudgetItemService busBudgetItemService) {
        this.busBudgetItemService = busBudgetItemService;
    }

    @Operation(summary = "创建预算明细")
    @PostMapping
    public Result<BusBudgetItem> create(@Valid @RequestBody BusBudgetItem item) {
        busBudgetItemService.save(item);
        return Result.ok(item);
    }

    @Operation(summary = "更新预算明细")
    @PutMapping("/{id}")
    public Result<BusBudgetItem> update(@PathVariable @NotNull Long id, @Valid @RequestBody BusBudgetItem item) {
        item.setId(id);
        busBudgetItemService.updateById(item);
        return Result.ok(item);
    }

    @Operation(summary = "根据ID查询预算明细")
    @GetMapping("/{id}")
    public Result<BusBudgetItem> getById(@PathVariable @NotNull Long id) {
        return Result.ok(busBudgetItemService.getById(id));
    }

    @Operation(summary = "分页查询预算明细")
    @GetMapping("/page")
    public Result<IPage<BusBudgetItem>> page(@RequestParam(defaultValue = "1") @Min(1) long page,
                                             @RequestParam(defaultValue = "10") @Min(1) long size) {
        return Result.ok(busBudgetItemService.page(new Page<>(page, size)));
    }

    @Operation(summary = "删除预算明细")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.ok(busBudgetItemService.removeById(id));
    }
}
