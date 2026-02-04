package com.ghost.moneyflowbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.entity.BusGoalRecord;
import com.ghost.moneyflowbackend.service.BusGoalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "目标记录管理")
@Validated
@RestController
@RequestMapping("/api/goal-records")
public class BusGoalRecordController {

    private final BusGoalRecordService busGoalRecordService;

    public BusGoalRecordController(BusGoalRecordService busGoalRecordService) {
        this.busGoalRecordService = busGoalRecordService;
    }

    @Operation(summary = "创建目标记录")
    @PostMapping
    public Result<BusGoalRecord> create(@Valid @RequestBody BusGoalRecord record) {
        busGoalRecordService.save(record);
        return Result.ok(record);
    }

    @Operation(summary = "更新目标记录")
    @PutMapping("/{id}")
    public Result<BusGoalRecord> update(@PathVariable @NotNull Long id, @Valid @RequestBody BusGoalRecord record) {
        record.setId(id);
        busGoalRecordService.updateById(record);
        return Result.ok(record);
    }

    @Operation(summary = "根据ID查询目标记录")
    @GetMapping("/{id}")
    public Result<BusGoalRecord> getById(@PathVariable @NotNull Long id) {
        return Result.ok(busGoalRecordService.getById(id));
    }

    @Operation(summary = "分页查询目标记录")
    @GetMapping("/page")
    public Result<IPage<BusGoalRecord>> page(@RequestParam(defaultValue = "1") @Min(1) long page,
                                             @RequestParam(defaultValue = "10") @Min(1) long size) {
        return Result.ok(busGoalRecordService.page(new Page<>(page, size)));
    }

    @Operation(summary = "删除目标记录")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.ok(busGoalRecordService.removeById(id));
    }
}
