package com.ghost.moneyflowbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghost.moneyflowbackend.entity.BusRecurringRule;
import com.ghost.moneyflowbackend.model.dto.RecurringRuleCreateRequest;
import com.ghost.moneyflowbackend.model.dto.RecurringRuleUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.RecurringRuleVO;
import com.ghost.moneyflowbackend.model.vo.TransactionVO;

import java.util.List;

public interface BusRecurringRuleService extends IService<BusRecurringRule> {
    List<RecurringRuleVO> listRules();

    RecurringRuleVO createRule(RecurringRuleCreateRequest request);

    RecurringRuleVO updateRule(Long ruleId, RecurringRuleUpdateRequest request);

    void deleteRule(Long ruleId);

    List<TransactionVO> generateTransactions();

    List<TransactionVO> generateAllTransactions();
}
