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
 * 交易明细实体
 */
@Data
@TableName("bus_transaction")
public class BusTransaction {

    /**
     * 交易ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 类型(expense, income, transfer)
     */
    @NotBlank(message = "交易类型不能为空")
    private String type;

    /**
     * 本位币金额
     */
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    /**
     * 原币种
     */
    private String currency;

    /**
     * 原币种金额
     */
    private BigDecimal origAmount;

    /**
     * 发生时间
     */
    @NotNull(message = "发生时间不能为空")
    private LocalDate date;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 主账户ID(支出/转出)
     */
    @NotNull(message = "主账户ID不能为空")
    private Long accountId;

    /**
     * 目标账户ID(转账专用)
     */
    private Long targetAccountId;

    /**
     * 备注
     */
    private String note;

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
