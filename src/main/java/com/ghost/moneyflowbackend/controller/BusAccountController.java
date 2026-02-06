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

    /**
     * 构造方法
     *
     * @param busAccountService 账户业务服务
     */
    public BusAccountController(BusAccountService busAccountService) {
        this.busAccountService = busAccountService;
    }

    /**
     * 获取账户列表
     *
     * @return 账户列表
     */
    @Operation(summary = "获取账户列表")
    @GetMapping
    public Result<List<AccountVO>> list() {
        return Result.ok(busAccountService.listAccounts());
    }

    /**
     * 创建账户
     *
     * @param request 创建参数
     * @return 创建后的账户信息
     */
    @Operation(summary = "创建账户")
    @PostMapping
    public Result<AccountVO> create(@Valid @RequestBody AccountCreateRequest request) {
        return Result.ok(busAccountService.createAccount(request));
    }

    /**
     * 更新账户
     *
     * @param id 账户ID
     * @param request 更新参数
     * @return 更新后的账户信息
     */
    @Operation(summary = "更新账户")
    @PutMapping("/{id}")
    public Result<AccountVO> update(@PathVariable("id") @NotNull(message = "账户ID不能为空") Long id,
                                    @RequestBody AccountUpdateRequest request) {
        return Result.ok(busAccountService.updateAccount(id, request));
    }

    /**
     * 删除账户
     *
     * @param id 账户ID
     * @return 删除结果
     */
    @Operation(summary = "删除账户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") @NotNull(message = "账户ID不能为空") Long id) {
        busAccountService.deleteAccount(id);
        return Result.ok(null);
    }
}
