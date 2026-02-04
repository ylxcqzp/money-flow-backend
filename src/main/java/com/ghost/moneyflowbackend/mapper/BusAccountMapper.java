package com.ghost.moneyflowbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghost.moneyflowbackend.entity.BusAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产账户数据访问层
 */
@Mapper
public interface BusAccountMapper extends BaseMapper<BusAccount> {
}
