package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.service.BusBudgetItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "预算明细管理")
@RestController
@RequestMapping("/api/budget-items")
public class BusBudgetItemController {

    private final BusBudgetItemService busBudgetItemService;

    /**
     * 构造方法
     *
     * @param busBudgetItemService 预算明细业务服务
     */
    public BusBudgetItemController(BusBudgetItemService busBudgetItemService) {
        this.busBudgetItemService = busBudgetItemService;
    }
}
