package com.ghost.moneyflowbackend.task;

import com.ghost.moneyflowbackend.model.vo.TransactionVO;
import com.ghost.moneyflowbackend.service.BusRecurringRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecurringRuleTask {

    private final BusRecurringRuleService busRecurringRuleService;

    @Scheduled(cron = "0 0 1 * * ?")
    public void executeRecurringRules() {
        try {
            List<TransactionVO> generatedTransactions = busRecurringRuleService.generateAllTransactions();
            if (generatedTransactions.isEmpty()) {
                log.info("周期性规则任务完成，无需生成交易记录");
                return;
            }
            log.info("周期性规则任务完成，共生成{}条交易记录", generatedTransactions.size());
        } catch (Exception exception) {
            log.error("周期性规则任务执行失败", exception);
        }
    }
}
