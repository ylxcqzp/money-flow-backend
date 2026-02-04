package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.entity.BusBudgetItem;
import com.ghost.moneyflowbackend.mapper.BusBudgetItemMapper;
import com.ghost.moneyflowbackend.service.BusBudgetItemService;
import org.springframework.stereotype.Service;

@Service
public class BusBudgetItemServiceImpl extends ServiceImpl<BusBudgetItemMapper, BusBudgetItem> implements BusBudgetItemService {
}
