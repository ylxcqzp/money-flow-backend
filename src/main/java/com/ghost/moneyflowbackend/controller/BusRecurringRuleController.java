package com.ghost.moneyflowbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.entity.BusRecurringRule;
import com.ghost.moneyflowbackend.service.BusRecurringRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "周期账单管理")
@Validated
@RestController
@RequestMapping("/api/recurring-rules")
public class BusRecurringRuleController {

    private final BusRecurringRuleService busRecurringRuleService;

    public BusRecurringRuleController(BusRecurringRuleService busRecurringRuleService) {
        this.busRecurringRuleService = busRecurringRuleService;
    }

    @Operation(summary = "创建周期账单规则")
    @PostMapping
    public Result<BusRecurringRule> create(@Valid @RequestBody BusRecurringRule rule) {
        busRecurringRuleService.save(rule);
        return Result.ok(rule);
    }

    @Operation(summary = "更新周期账单规则")
    @PutMapping("/{id}")
    public Result<BusRecurringRule> update(@PathVariable @NotNull Long id, @Valid @RequestBody BusRecurringRule rule) {
        rule.setId(id);
        busRecurringRuleService.updateById(rule);
        return Result.ok(rule);
    }

    @Operation(summary = "根据ID查询周期账单规则")
    @GetMapping("/{id}")
    public Result<BusRecurringRule> getById(@PathVariable @NotNull Long id) {
        return Result.ok(busRecurringRuleService.getById(id));
    }

    @Operation(summary = "分页查询周期账单规则")
    @GetMapping("/page")
    public Result<IPage<BusRecurringRule>> page(@RequestParam(defaultValue = "1") @Min(1) long page,
                                                @RequestParam(defaultValue = "10") @Min(1) long size) {
        return Result.ok(busRecurringRuleService.page(new Page<>(page, size)));
    }

    @Operation(summary = "删除周期账单规则")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.ok(busRecurringRuleService.removeById(id));
    }
}
