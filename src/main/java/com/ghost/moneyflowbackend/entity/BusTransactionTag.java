package com.ghost.moneyflowbackend.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 交易标签关联实体
 */
@Data
@TableName("bus_transaction_tag")
public class BusTransactionTag {

    /**
     * 交易ID
     */
    @NotNull(message = "交易ID不能为空")
    @TableField("transaction_id")
    private Long transactionId;

    /**
     * 标签ID
     */
    @NotNull(message = "标签ID不能为空")
    @TableField("tag_id")
    private Long tagId;

    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;
}
