package com.ghost.moneyflowbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.entity.BusTransactionTag;
import com.ghost.moneyflowbackend.service.BusTransactionTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "交易标签关联管理")
@Validated
@RestController
@RequestMapping("/api/transaction-tags")
public class BusTransactionTagController {

    private final BusTransactionTagService busTransactionTagService;

    public BusTransactionTagController(BusTransactionTagService busTransactionTagService) {
        this.busTransactionTagService = busTransactionTagService;
    }

    @Operation(summary = "创建交易标签关联")
    @PostMapping
    public Result<BusTransactionTag> create(@Valid @RequestBody BusTransactionTag tag) {
        busTransactionTagService.save(tag);
        return Result.ok(tag);
    }

    @Operation(summary = "更新交易标签关联")
    @PutMapping("/{transactionId}/{tagId}")
    public Result<Boolean> update(@PathVariable @NotNull Long transactionId,
                                  @PathVariable @NotNull Long tagId,
                                  @Valid @RequestBody BusTransactionTag body) {
        Long newTransactionId = body.getTransactionId() == null ? transactionId : body.getTransactionId();
        Long newTagId = body.getTagId() == null ? tagId : body.getTagId();
        LambdaUpdateWrapper<BusTransactionTag> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(BusTransactionTag::getTransactionId, transactionId)
                .eq(BusTransactionTag::getTagId, tagId)
                .set(BusTransactionTag::getTransactionId, newTransactionId)
                .set(BusTransactionTag::getTagId, newTagId);
        return Result.ok(busTransactionTagService.update(wrapper));
    }

    @Operation(summary = "根据ID查询交易标签关联")
    @GetMapping("/one")
    public Result<BusTransactionTag> getOne(@RequestParam @NotNull Long transactionId,
                                            @RequestParam @NotNull Long tagId) {
        LambdaQueryWrapper<BusTransactionTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusTransactionTag::getTransactionId, transactionId)
                .eq(BusTransactionTag::getTagId, tagId);
        return Result.ok(busTransactionTagService.getOne(wrapper));
    }

    @Operation(summary = "分页查询交易标签关联")
    @GetMapping("/page")
    public Result<IPage<BusTransactionTag>> page(@RequestParam(defaultValue = "1") @Min(1) long page,
                                                 @RequestParam(defaultValue = "10") @Min(1) long size) {
        return Result.ok(busTransactionTagService.page(new Page<>(page, size)));
    }

    @Operation(summary = "删除交易标签关联")
    @DeleteMapping
    public Result<Boolean> delete(@RequestParam @NotNull Long transactionId,
                                  @RequestParam @NotNull Long tagId) {
        LambdaQueryWrapper<BusTransactionTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusTransactionTag::getTransactionId, transactionId)
                .eq(BusTransactionTag::getTagId, tagId);
        return Result.ok(busTransactionTagService.remove(wrapper));
    }
}
