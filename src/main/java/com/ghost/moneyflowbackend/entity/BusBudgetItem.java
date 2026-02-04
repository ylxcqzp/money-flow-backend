package com.ghost.moneyflowbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;

/**
 * 分类预算明细实体
 */
@Data
@TableName("bus_budget_item")
public class BusBudgetItem {

    /**
     * 明细ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 主预算ID
     */
    @NotNull(message = "主预算ID不能为空")
    private Long budgetId;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 分类预算金额
     */
    @NotNull(message = "分类预算金额不能为空")
    private BigDecimal amount;

    /**
     * 删除标志
     */
    @TableLogic
    private Integer delFlag;
}
