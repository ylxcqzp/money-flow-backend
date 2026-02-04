package com.ghost.moneyflowbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 储蓄目标实体
 */
@Data
@TableName("bus_goal")
public class BusGoal {

    /**
     * 目标ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 目标名称
     */
    @NotBlank(message = "目标名称不能为空")
    private String name;

    /**
     * 目标金额
     */
    @NotNull(message = "目标金额不能为空")
    private BigDecimal targetAmount;

    /**
     * 当前筹集金额
     */
    private BigDecimal currentAmount;

    /**
     * 截止日期
     */
    private LocalDate deadline;

    /**
     * 图标
     */
    private String icon;

    /**
     * 颜色
     */
    private String color;

    /**
     * 状态(ongoing, completed, archived)
     */
    private String status;

    /**
     * 删除标志
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
