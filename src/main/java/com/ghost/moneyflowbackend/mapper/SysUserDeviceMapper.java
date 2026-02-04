package com.ghost.moneyflowbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghost.moneyflowbackend.entity.SysUserDevice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户设备信息数据访问层
 */
@Mapper
public interface SysUserDeviceMapper extends BaseMapper<SysUserDevice> {
}
