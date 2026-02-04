package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.entity.BusTransaction;
import com.ghost.moneyflowbackend.mapper.BusTransactionMapper;
import com.ghost.moneyflowbackend.service.BusTransactionService;
import org.springframework.stereotype.Service;

/**
 * 交易明细业务服务实现
 */
@Service
public class BusTransactionServiceImpl extends ServiceImpl<BusTransactionMapper, BusTransaction> implements BusTransactionService {
}
