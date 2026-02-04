package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.entity.BusCategory;
import com.ghost.moneyflowbackend.mapper.BusCategoryMapper;
import com.ghost.moneyflowbackend.service.BusCategoryService;
import org.springframework.stereotype.Service;

/**
 * 收支分类业务服务实现
 */
@Service
public class BusCategoryServiceImpl extends ServiceImpl<BusCategoryMapper, BusCategory> implements BusCategoryService {
}
