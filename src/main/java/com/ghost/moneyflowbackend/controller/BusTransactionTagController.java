package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.service.BusTransactionTagService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "交易标签关联管理")
@RestController
@RequestMapping("/api/transaction-tags")
public class BusTransactionTagController {

    private final BusTransactionTagService busTransactionTagService;

    public BusTransactionTagController(BusTransactionTagService busTransactionTagService) {
        this.busTransactionTagService = busTransactionTagService;
    }
}
