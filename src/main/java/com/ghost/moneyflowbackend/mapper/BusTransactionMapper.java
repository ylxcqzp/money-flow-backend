package com.ghost.moneyflowbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghost.moneyflowbackend.entity.BusTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 交易明细数据访问层
 */
@Mapper
public interface BusTransactionMapper extends BaseMapper<BusTransaction> {
    @Select("SELECT COALESCE(SUM(amount), 0) FROM bus_transaction " +
            "WHERE user_id = #{userId} AND del_flag = 0 AND type = 'income' AND account_id = #{accountId}")
    BigDecimal sumIncome(@Param("userId") Long userId, @Param("accountId") Long accountId);

    @Select("SELECT COALESCE(SUM(amount), 0) FROM bus_transaction " +
            "WHERE user_id = #{userId} AND del_flag = 0 AND type = 'expense' AND account_id = #{accountId}")
    BigDecimal sumExpense(@Param("userId") Long userId, @Param("accountId") Long accountId);

    @Select("SELECT COALESCE(SUM(amount), 0) FROM bus_transaction " +
            "WHERE user_id = #{userId} AND del_flag = 0 AND type = 'transfer' AND account_id = #{accountId}")
    BigDecimal sumTransferOut(@Param("userId") Long userId, @Param("accountId") Long accountId);

    @Select("SELECT COALESCE(SUM(amount), 0) FROM bus_transaction " +
            "WHERE user_id = #{userId} AND del_flag = 0 AND type = 'transfer' AND target_account_id = #{accountId}")
    BigDecimal sumTransferIn(@Param("userId") Long userId, @Param("accountId") Long accountId);

    @Select("SELECT COUNT(1) FROM bus_transaction " +
            "WHERE user_id = #{userId} AND del_flag = 0 AND (account_id = #{accountId} OR target_account_id = #{accountId})")
    long countByAccount(@Param("userId") Long userId, @Param("accountId") Long accountId);

    @Select("SELECT COUNT(1) FROM bus_transaction " +
            "WHERE user_id = #{userId} AND del_flag = 0 AND category_id = #{categoryId}")
    long countByCategory(@Param("userId") Long userId, @Param("categoryId") Long categoryId);
}
