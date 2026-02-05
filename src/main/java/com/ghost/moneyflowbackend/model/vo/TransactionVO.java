package com.ghost.moneyflowbackend.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TransactionVO {
    private Long id;
    private String type;
    private BigDecimal amount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private Long categoryId;
    private Long accountId;
    private Long targetAccountId;
    private String note;
    private List<String> tags;
}
