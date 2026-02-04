package com.ghost.moneyflowbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.entity.BusGoal;
import com.ghost.moneyflowbackend.service.BusGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "储蓄目标管理")
@Validated
@RestController
@RequestMapping("/api/goals")
public class BusGoalController {

    private final BusGoalService busGoalService;

    public BusGoalController(BusGoalService busGoalService) {
        this.busGoalService = busGoalService;
    }

    @Operation(summary = "创建储蓄目标")
    @PostMapping
    public Result<BusGoal> create(@Valid @RequestBody BusGoal goal) {
        busGoalService.save(goal);
        return Result.ok(goal);
    }

    @Operation(summary = "更新储蓄目标")
    @PutMapping("/{id}")
    public Result<BusGoal> update(@PathVariable @NotNull Long id, @Valid @RequestBody BusGoal goal) {
        goal.setId(id);
        busGoalService.updateById(goal);
        return Result.ok(goal);
    }

    @Operation(summary = "根据ID查询储蓄目标")
    @GetMapping("/{id}")
    public Result<BusGoal> getById(@PathVariable @NotNull Long id) {
        return Result.ok(busGoalService.getById(id));
    }

    @Operation(summary = "分页查询储蓄目标")
    @GetMapping("/page")
    public Result<IPage<BusGoal>> page(@RequestParam(defaultValue = "1") @Min(1) long page,
                                       @RequestParam(defaultValue = "10") @Min(1) long size) {
        return Result.ok(busGoalService.page(new Page<>(page, size)));
    }

    @Operation(summary = "删除储蓄目标")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.ok(busGoalService.removeById(id));
    }
}
