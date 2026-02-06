package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.model.dto.GoalCreateRequest;
import com.ghost.moneyflowbackend.model.dto.GoalRecordCreateRequest;
import com.ghost.moneyflowbackend.model.dto.GoalUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.GoalVO;
import com.ghost.moneyflowbackend.service.BusGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "储蓄目标管理")
@Validated
@RestController
@RequestMapping("/api/goals")
public class BusGoalController {

    private final BusGoalService busGoalService;

    /**
     * 构造方法
     *
     * @param busGoalService 储蓄目标业务服务
     */
    public BusGoalController(BusGoalService busGoalService) {
        this.busGoalService = busGoalService;
    }

    /**
     * 获取目标列表
     *
     * @return 目标列表
     */
    @Operation(summary = "获取目标列表")
    @GetMapping
    public Result<List<GoalVO>> list() {
        return Result.ok(busGoalService.listGoals());
    }

    /**
     * 创建目标
     *
     * @param request 创建参数
     * @return 创建后的目标信息
     */
    @Operation(summary = "创建目标")
    @PostMapping
    public Result<GoalVO> create(@Valid @RequestBody GoalCreateRequest request) {
        return Result.ok(busGoalService.createGoal(request));
    }

    /**
     * 更新目标
     *
     * @param id 目标ID
     * @param request 更新参数
     * @return 更新后的目标信息
     */
    @Operation(summary = "更新目标")
    @PutMapping("/{id}")
    public Result<GoalVO> update(@PathVariable("id") @NotNull(message = "目标ID不能为空") Long id,
                                 @RequestBody GoalUpdateRequest request) {
        return Result.ok(busGoalService.updateGoal(id, request));
    }

    /**
     * 删除目标
     *
     * @param id 目标ID
     * @return 删除结果
     */
    @Operation(summary = "删除目标")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") @NotNull(message = "目标ID不能为空") Long id) {
        busGoalService.deleteGoal(id);
        return Result.ok(null);
    }

    /**
     * 新增目标存取记录
     *
     * @param id 目标ID
     * @param request 存取记录参数
     * @return 更新后的目标信息
     */
    @Operation(summary = "新增目标存取记录")
    @PostMapping("/{id}/records")
    public Result<GoalVO> createRecord(@PathVariable("id") @NotNull(message = "目标ID不能为空") Long id,
                                      @Valid @RequestBody GoalRecordCreateRequest request) {
        return Result.ok(busGoalService.createRecord(id, request));
    }
}
