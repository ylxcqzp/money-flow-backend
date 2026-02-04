package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.entity.SysUser;
import com.ghost.moneyflowbackend.mapper.SysUserMapper;
import com.ghost.moneyflowbackend.service.SysUserService;
import org.springframework.stereotype.Service;

/**
 * 用户业务服务实现
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
}
