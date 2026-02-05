package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.model.dto.BudgetSaveRequest;
import com.ghost.moneyflowbackend.model.vo.BudgetVO;
import com.ghost.moneyflowbackend.service.BusBudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

    @Operation(summary = "获取月度预算")
    @GetMapping
    public Result<BudgetVO> getBudget(@RequestParam("month") @NotBlank(message = "月份不能为空") String month) {
        return Result.ok(busBudgetService.getBudget(month));
    }

    @Operation(summary = "设置/更新预算")
    @PostMapping
    public Result<BudgetVO> save(@Valid @RequestBody BudgetSaveRequest request) {
        return Result.ok(busBudgetService.saveBudget(request));
    }
}
