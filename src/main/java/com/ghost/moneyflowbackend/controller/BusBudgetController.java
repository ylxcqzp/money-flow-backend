package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.service.BusBudgetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "预算管理")
@RestController
@RequestMapping("/api/budgets")
public class BusBudgetController {

    private final BusBudgetService busBudgetService;

    public BusBudgetController(BusBudgetService busBudgetService) {
        this.busBudgetService = busBudgetService;
    }
}
