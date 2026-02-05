package com.ghost.moneyflowbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghost.moneyflowbackend.entity.BusBudget;
import com.ghost.moneyflowbackend.model.dto.BudgetSaveRequest;
import com.ghost.moneyflowbackend.model.vo.BudgetVO;

public interface BusBudgetService extends IService<BusBudget> {
    BudgetVO getBudget(String month);

    BudgetVO saveBudget(BudgetSaveRequest request);
}
