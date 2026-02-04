package com.ghost.moneyflowbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghost.moneyflowbackend.entity.BusTransactionTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 交易标签关联数据访问层
 */
@Mapper
public interface BusTransactionTagMapper extends BaseMapper<BusTransactionTag> {
}
