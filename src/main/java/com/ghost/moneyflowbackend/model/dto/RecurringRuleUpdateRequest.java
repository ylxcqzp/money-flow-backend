package com.ghost.moneyflowbackend.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecurringRuleUpdateRequest {
    private String type;
    private BigDecimal amount;
    private String frequency;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    private Long categoryId;
    private Long accountId;
    @Size(max = 200, message = "描述长度不能超过200")
    private String description;
    private Integer status;
}
