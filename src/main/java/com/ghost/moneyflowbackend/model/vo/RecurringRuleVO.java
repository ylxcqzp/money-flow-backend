package com.ghost.moneyflowbackend.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RecurringRuleVO {
    private Long id;
    private String type;
    private BigDecimal amount;
    private Long categoryId;
    private Long accountId;
    private String frequency;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextExecutionDate;
    private Integer status;
}
