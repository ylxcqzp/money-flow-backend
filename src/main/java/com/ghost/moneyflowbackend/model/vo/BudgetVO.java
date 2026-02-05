package com.ghost.moneyflowbackend.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class BudgetVO {
    private String month;
    private BigDecimal total;
    private Map<Long, BigDecimal> categories = new HashMap<>();
}
