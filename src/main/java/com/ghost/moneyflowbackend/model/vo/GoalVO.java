package com.ghost.moneyflowbackend.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GoalVO {
    private Long id;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;
    private String icon;
    private String color;
    private String status;
}
