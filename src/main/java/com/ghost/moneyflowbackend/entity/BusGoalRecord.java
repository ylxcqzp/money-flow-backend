package com.ghost.moneyflowbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotNull;

/**
 * 目标存取记录实体
 */
@Data
@TableName("bus_goal_record")
public class BusGoalRecord {

    /**
     * 流水ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 目标ID
     */
    @NotNull(message = "目标ID不能为空")
    private Long goalId;

    /**
     * 操作金额
     */
    @NotNull(message = "操作金额不能为空")
    private BigDecimal amount;

    /**
     * 日期
     */
    @NotNull(message = "日期不能为空")
    private LocalDate operateDate;

    /**
     * 操作人
     */
    private Long createBy;

    /**
     * 记录时间
     */
    private LocalDateTime createTime;
}
