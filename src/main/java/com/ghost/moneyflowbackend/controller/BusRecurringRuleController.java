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

    /**
     * 构造方法
     *
     * @param busRecurringRuleService 周期账单业务服务
     */
    public BusRecurringRuleController(BusRecurringRuleService busRecurringRuleService) {
        this.busRecurringRuleService = busRecurringRuleService;
    }

    /**
     * 获取规则列表
     *
     * @return 规则列表
     */
    @Operation(summary = "获取规则列表")
    @GetMapping
    public Result<List<RecurringRuleVO>> list() {
        return Result.ok(busRecurringRuleService.listRules());
    }

    /**
     * 创建规则
     *
     * @param request 创建参数
     * @return 创建后的规则信息
     */
    @Operation(summary = "创建规则")
    @PostMapping
    public Result<RecurringRuleVO> create(@Valid @RequestBody RecurringRuleCreateRequest request) {
        return Result.ok(busRecurringRuleService.createRule(request));
    }

    /**
     * 更新规则
     *
     * @param id 规则ID
     * @param request 更新参数
     * @return 更新后的规则信息
     */
    @Operation(summary = "更新规则")
    @PutMapping("/{id}")
    public Result<RecurringRuleVO> update(@PathVariable("id") @NotNull(message = "规则ID不能为空") Long id,
                                          @RequestBody RecurringRuleUpdateRequest request) {
        return Result.ok(busRecurringRuleService.updateRule(id, request));
    }

    /**
     * 删除规则
     *
     * @param id 规则ID
     * @return 删除结果
     */
    @Operation(summary = "删除规则")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") @NotNull(message = "规则ID不能为空") Long id) {
        busRecurringRuleService.deleteRule(id);
        return Result.ok(null);
    }

    /**
     * 触发生成周期性交易
     *
     * @return 生成结果及交易列表
     */
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
