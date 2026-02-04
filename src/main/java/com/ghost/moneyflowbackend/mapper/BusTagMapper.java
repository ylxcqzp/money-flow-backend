package com.ghost.moneyflowbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghost.moneyflowbackend.entity.BusTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签数据访问层
 */
@Mapper
public interface BusTagMapper extends BaseMapper<BusTag> {
}
