package com.ghost.moneyflowbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghost.moneyflowbackend.entity.BusCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收支分类数据访问层
 */
@Mapper
public interface BusCategoryMapper extends BaseMapper<BusCategory> {
}
