package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.model.dto.RecurringRuleCreateRequest;
import com.ghost.moneyflowbackend.model.dto.RecurringRuleUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.RecurringRuleVO;
import com.ghost.moneyflowbackend.model.vo.TransactionVO;
import com.ghost.moneyflowbackend.service.BusRecurringRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "周期账单管理")
@Validated
@RestController
@RequestMapping("/api/recurring-rules")
public class BusRecurringRuleController {

    private final BusRecurringRuleService busRecurringRuleService;

    public BusRecurringRuleController(BusRecurringRuleService busRecurringRuleService) {
        this.busRecurringRuleService = busRecurringRuleService;
    }

    @Operation(summary = "获取规则列表")
    @GetMapping
    public Result<List<RecurringRuleVO>> list() {
        return Result.ok(busRecurringRuleService.listRules());
    }

    @Operation(summary = "创建规则")
    @PostMapping
    public Result<RecurringRuleVO> create(@Valid @RequestBody RecurringRuleCreateRequest request) {
        return Result.ok(busRecurringRuleService.createRule(request));
    }

    @Operation(summary = "更新规则")
    @PutMapping("/{id}")
    public Result<RecurringRuleVO> update(@PathVariable("id") @NotNull(message = "规则ID不能为空") Long id,
                                          @RequestBody RecurringRuleUpdateRequest request) {
        return Result.ok(busRecurringRuleService.updateRule(id, request));
    }

    @Operation(summary = "删除规则")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") @NotNull(message = "规则ID不能为空") Long id) {
        busRecurringRuleService.deleteRule(id);
        return Result.ok(null);
    }

    @Operation(summary = "生成周期性交易")
    @PostMapping("/generate")
    public Result<Map<String, Object>> generate() {
        List<TransactionVO> transactions = busRecurringRuleService.generateTransactions();
        Map<String, Object> data = new HashMap<>();
        data.put("generated", transactions.size());
        data.put("transactions", transactions);
        return Result.ok(data);
    }
}
