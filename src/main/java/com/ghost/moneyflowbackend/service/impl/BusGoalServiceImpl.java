package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.entity.BusGoal;
import com.ghost.moneyflowbackend.mapper.BusGoalMapper;
import com.ghost.moneyflowbackend.service.BusGoalService;
import org.springframework.stereotype.Service;

@Service
public class BusGoalServiceImpl extends ServiceImpl<BusGoalMapper, BusGoal> implements BusGoalService {
}
