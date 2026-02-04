package com.ghost.moneyflowbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.entity.BusBudget;
import com.ghost.moneyflowbackend.service.BusBudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "预算管理")
@Validated
@RestController
@RequestMapping("/api/budgets")
public class BusBudgetController {

    private final BusBudgetService busBudgetService;

    public BusBudgetController(BusBudgetService busBudgetService) {
        this.busBudgetService = busBudgetService;
    }

    @Operation(summary = "创建预算")
    @PostMapping
    public Result<BusBudget> create(@Valid @RequestBody BusBudget budget) {
        busBudgetService.save(budget);
        return Result.ok(budget);
    }

    @Operation(summary = "更新预算")
    @PutMapping("/{id}")
    public Result<BusBudget> update(@PathVariable @NotNull Long id, @Valid @RequestBody BusBudget budget) {
        budget.setId(id);
        busBudgetService.updateById(budget);
        return Result.ok(budget);
    }

    @Operation(summary = "根据ID查询预算")
    @GetMapping("/{id}")
    public Result<BusBudget> getById(@PathVariable @NotNull Long id) {
        return Result.ok(busBudgetService.getById(id));
    }

    @Operation(summary = "分页查询预算")
    @GetMapping("/page")
    public Result<IPage<BusBudget>> page(@RequestParam(defaultValue = "1") @Min(1) long page,
                                         @RequestParam(defaultValue = "10") @Min(1) long size) {
        return Result.ok(busBudgetService.page(new Page<>(page, size)));
    }

    @Operation(summary = "删除预算")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.ok(busBudgetService.removeById(id));
    }
}
