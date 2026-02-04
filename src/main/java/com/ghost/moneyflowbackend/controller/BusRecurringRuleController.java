package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.service.BusRecurringRuleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "周期账单管理")
@RestController
@RequestMapping("/api/recurring-rules")
public class BusRecurringRuleController {

    private final BusRecurringRuleService busRecurringRuleService;

    public BusRecurringRuleController(BusRecurringRuleService busRecurringRuleService) {
        this.busRecurringRuleService = busRecurringRuleService;
    }
}
