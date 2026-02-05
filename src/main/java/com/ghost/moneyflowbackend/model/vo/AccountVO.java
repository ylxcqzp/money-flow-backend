package com.ghost.moneyflowbackend.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountVO {
    private Long id;
    private String name;
    private String type;
    private String icon;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private Integer sortOrder;
}
