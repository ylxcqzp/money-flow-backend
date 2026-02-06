package com.ghost.moneyflowbackend.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class TransactionCreateRequest {
    @NotBlank(message = "交易类型不能为空")
    private String type;

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;

    private String currency;

    private BigDecimal originalAmount;

    @NotNull(message = "发生时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Long categoryId;

    @NotNull(message = "主账户ID不能为空")
    private Long accountId;

    private Long targetAccountId;

    private String note;

    private List<String> tags;
}
