package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.entity.SysLoginLog;
import com.ghost.moneyflowbackend.mapper.SysLoginLogMapper;
import com.ghost.moneyflowbackend.service.SysLoginLogService;
import org.springframework.stereotype.Service;

/**
 * 登录日志业务服务实现
 */
@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {
}
