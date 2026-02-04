package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.entity.BusTag;
import com.ghost.moneyflowbackend.mapper.BusTagMapper;
import com.ghost.moneyflowbackend.service.BusTagService;
import org.springframework.stereotype.Service;

/**
 * 标签业务服务实现
 */
@Service
public class BusTagServiceImpl extends ServiceImpl<BusTagMapper, BusTag> implements BusTagService {
}
