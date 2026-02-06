package com.ghost.moneyflowbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 周期性账单规则实体
 */
@Data
@TableName("bus_recurring_rule")
public class BusRecurringRule {

    /**
     * 规则ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 类型(expense, income)
     */
    @NotBlank(message = "类型不能为空")
    private String type;

    /**
     * 金额
     */
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    /**
     * 账户ID
     */
    @NotNull(message = "账户ID不能为空")
    private Long accountId;

    private String description;

    /**
     * 频率(daily, weekly, monthly, yearly)
     */
    @NotBlank(message = "频率不能为空")
    private String frequency;

    /**
     * 开始日期
     */
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    /**
     * 下次执行日期
     */
    @NotNull(message = "下次执行日期不能为空")
    private LocalDate nextExecutionDate;

    /**
     * 状态(0启用 1停用)
     */
    private Integer status;

    /**
     * 删除标志
     */
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
