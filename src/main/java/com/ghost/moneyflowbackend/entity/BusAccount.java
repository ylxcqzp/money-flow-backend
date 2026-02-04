package com.ghost.moneyflowbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 资产账户实体
 */
@Data
@TableName("bus_account")
public class BusAccount {

    /**
     * 账户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 账户名称
     */
    @NotBlank(message = "账户名称不能为空")
    private String name;

    /**
     * 账户类型(cash, card, alipay, wechat, other)
     */
    @NotBlank(message = "账户类型不能为空")
    private String type;

    /**
     * 图标名称
     */
    private String icon;

    /**
     * 初始余额
     */
    @NotNull(message = "初始余额不能为空")
    private BigDecimal initialBalance;

    /**
     * 排序权重
     */
    private Integer sortOrder;

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
