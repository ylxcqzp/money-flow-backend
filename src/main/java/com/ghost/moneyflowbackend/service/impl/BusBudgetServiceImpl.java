package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.entity.BusBudget;
import com.ghost.moneyflowbackend.mapper.BusBudgetMapper;
import com.ghost.moneyflowbackend.service.BusBudgetService;
import org.springframework.stereotype.Service;

@Service
public class BusBudgetServiceImpl extends ServiceImpl<BusBudgetMapper, BusBudget> implements BusBudgetService {
}
