package com.ghost.moneyflowbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghost.moneyflowbackend.entity.BusBudget;
import org.apache.ibatis.annotations.Mapper;

/**
 * 月度预算数据访问层
 */
@Mapper
public interface BusBudgetMapper extends BaseMapper<BusBudget> {
}
