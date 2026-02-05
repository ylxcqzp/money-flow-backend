package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.model.dto.AccountCreateRequest;
import com.ghost.moneyflowbackend.model.dto.AccountUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.AccountVO;
import com.ghost.moneyflowbackend.service.BusAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "资产账户管理")
@Validated
@RestController
@RequestMapping("/api/accounts")
public class BusAccountController {

    private final BusAccountService busAccountService;

    public BusAccountController(BusAccountService busAccountService) {
        this.busAccountService = busAccountService;
    }

    @Operation(summary = "获取账户列表")
    @GetMapping
    public Result<List<AccountVO>> list() {
        return Result.ok(busAccountService.listAccounts());
    }

    @Operation(summary = "创建账户")
    @PostMapping
    public Result<AccountVO> create(@Valid @RequestBody AccountCreateRequest request) {
        return Result.ok(busAccountService.createAccount(request));
    }

    @Operation(summary = "更新账户")
    @PutMapping("/{id}")
    public Result<AccountVO> update(@PathVariable("id") @NotNull(message = "账户ID不能为空") Long id,
                                    @RequestBody AccountUpdateRequest request) {
        return Result.ok(busAccountService.updateAccount(id, request));
    }

    @Operation(summary = "删除账户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") @NotNull(message = "账户ID不能为空") Long id) {
        busAccountService.deleteAccount(id);
        return Result.ok(null);
    }
}
