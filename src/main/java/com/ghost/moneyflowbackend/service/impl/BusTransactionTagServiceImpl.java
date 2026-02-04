package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.entity.BusTransactionTag;
import com.ghost.moneyflowbackend.mapper.BusTransactionTagMapper;
import com.ghost.moneyflowbackend.service.BusTransactionTagService;
import org.springframework.stereotype.Service;

/**
 * 交易标签关联业务服务实现
 */
@Service
public class BusTransactionTagServiceImpl extends ServiceImpl<BusTransactionTagMapper, BusTransactionTag> implements BusTransactionTagService {
}
