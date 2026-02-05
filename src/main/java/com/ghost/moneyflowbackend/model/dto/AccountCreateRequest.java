package com.ghost.moneyflowbackend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountCreateRequest {
    @NotBlank(message = "账户名称不能为空")
    private String name;

    @NotBlank(message = "账户类型不能为空")
    private String type;

    private String icon;

    @NotNull(message = "初始余额不能为空")
    private BigDecimal initialBalance;

    private Integer sortOrder;
}
