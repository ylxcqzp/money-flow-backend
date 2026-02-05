package com.ghost.moneyflowbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghost.moneyflowbackend.entity.BusGoal;
import com.ghost.moneyflowbackend.model.dto.GoalCreateRequest;
import com.ghost.moneyflowbackend.model.dto.GoalRecordCreateRequest;
import com.ghost.moneyflowbackend.model.dto.GoalUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.GoalVO;

import java.util.List;

public interface BusGoalService extends IService<BusGoal> {
    List<GoalVO> listGoals();

    GoalVO createGoal(GoalCreateRequest request);

    GoalVO updateGoal(Long goalId, GoalUpdateRequest request);

    void deleteGoal(Long goalId);

    GoalVO createRecord(Long goalId, GoalRecordCreateRequest request);
}
