package com.ghost.moneyflowbackend.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GoalUpdateRequest {
    private String name;
    private BigDecimal targetAmount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;
    private String icon;
    private String color;
    private String status;
}
