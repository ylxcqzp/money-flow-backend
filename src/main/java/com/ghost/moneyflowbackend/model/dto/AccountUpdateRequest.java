package com.ghost.moneyflowbackend.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountUpdateRequest {
    private String name;
    private String type;
    private String icon;
    private BigDecimal initialBalance;
    private Integer sortOrder;
}
