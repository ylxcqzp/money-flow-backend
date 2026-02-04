package com.ghost.moneyflowbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghost.moneyflowbackend.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表数据访问层
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
