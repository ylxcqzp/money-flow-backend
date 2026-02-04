package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.entity.BusAccount;
import com.ghost.moneyflowbackend.mapper.BusAccountMapper;
import com.ghost.moneyflowbackend.service.BusAccountService;
import org.springframework.stereotype.Service;

/**
 * 资产账户业务服务实现
 */
@Service
public class BusAccountServiceImpl extends ServiceImpl<BusAccountMapper, BusAccount> implements BusAccountService {
}
