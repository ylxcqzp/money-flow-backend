package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.entity.BusGoalRecord;
import com.ghost.moneyflowbackend.mapper.BusGoalRecordMapper;
import com.ghost.moneyflowbackend.service.BusGoalRecordService;
import org.springframework.stereotype.Service;

@Service
public class BusGoalRecordServiceImpl extends ServiceImpl<BusGoalRecordMapper, BusGoalRecord> implements BusGoalRecordService {
}
