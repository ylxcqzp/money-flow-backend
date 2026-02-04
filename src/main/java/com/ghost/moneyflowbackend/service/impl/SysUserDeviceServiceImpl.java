package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.entity.SysUserDevice;
import com.ghost.moneyflowbackend.mapper.SysUserDeviceMapper;
import com.ghost.moneyflowbackend.service.SysUserDeviceService;
import org.springframework.stereotype.Service;

/**
 * 用户设备业务服务实现
 */
@Service
public class SysUserDeviceServiceImpl extends ServiceImpl<SysUserDeviceMapper, SysUserDevice> implements SysUserDeviceService {
}
