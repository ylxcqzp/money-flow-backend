package com.ghost.moneyflowbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghost.moneyflowbackend.entity.BusTransaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 交易明细数据访问层
 */
@Mapper
public interface BusTransactionMapper extends BaseMapper<BusTransaction> {
}
