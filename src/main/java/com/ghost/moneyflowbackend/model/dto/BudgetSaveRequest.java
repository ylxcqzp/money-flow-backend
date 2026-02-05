package com.ghost.moneyflowbackend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class BudgetSaveRequest {
    @NotBlank(message = "月份不能为空")
    private String month;

    @NotNull(message = "总预算金额不能为空")
    private BigDecimal total;

    private Map<Long, BigDecimal> categories;
}
