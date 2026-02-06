package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.model.dto.TransactionCreateRequest;
import com.ghost.moneyflowbackend.model.dto.TransactionUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.TransactionVO;
import com.ghost.moneyflowbackend.service.BusTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "交易明细管理")
@Validated
@RestController
@RequestMapping("/api/transactions")
public class BusTransactionController {

    private final BusTransactionService busTransactionService;

    /**
     * 构造方法
     *
     * @param busTransactionService 交易明细业务服务
     */
    public BusTransactionController(BusTransactionService busTransactionService) {
        this.busTransactionService = busTransactionService;
    }

    /**
     * 获取交易列表
     *
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param type 交易类型（可选）
     * @param categoryId 分类ID（可选）
     * @param accountId 账户ID（可选）
     * @param tags 标签列表，逗号分隔（可选）
     * @return 交易列表
     */
    @Operation(summary = "获取交易列表")
    @GetMapping
    public Result<List<TransactionVO>> list(@RequestParam(value = "startDate", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                            @RequestParam(value = "endDate", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                            @RequestParam(value = "type", required = false) String type,
                                            @RequestParam(value = "categoryId", required = false) Long categoryId,
                                            @RequestParam(value = "accountId", required = false) Long accountId,
                                            @RequestParam(value = "tags", required = false) String tags) {
        List<String> tagList = null;
        if (tags != null && !tags.isBlank()) {
            tagList = List.of(tags.split(","));
        }
        return Result.ok(busTransactionService.listTransactions(startDate, endDate, type, categoryId, accountId, tagList));
    }

    /**
     * 创建交易
     *
     * @param request 创建参数
     * @return 创建后的交易信息
     */
    @Operation(summary = "创建交易")
    @PostMapping
    public Result<TransactionVO> create(@Valid @RequestBody TransactionCreateRequest request) {
        return Result.ok(busTransactionService.createTransaction(request));
    }

    /**
     * 更新交易
     *
     * @param id 交易ID
     * @param request 更新参数
     * @return 更新后的交易信息
     */
    @Operation(summary = "更新交易")
    @PutMapping("/{id}")
    public Result<TransactionVO> update(@PathVariable("id") @NotNull(message = "交易ID不能为空") Long id,
                                       @RequestBody TransactionUpdateRequest request) {
        return Result.ok(busTransactionService.updateTransaction(id, request));
    }

    /**
     * 删除交易
     *
     * @param id 交易ID
     * @return 删除结果
     */
    @Operation(summary = "删除交易")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") @NotNull(message = "交易ID不能为空") Long id) {
        busTransactionService.deleteTransaction(id);
        return Result.ok(null);
    }
}
