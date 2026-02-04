package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.service.BusTransactionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "交易明细管理")
@RestController
@RequestMapping("/api/transactions")
public class BusTransactionController {

    private final BusTransactionService busTransactionService;

    public BusTransactionController(BusTransactionService busTransactionService) {
        this.busTransactionService = busTransactionService;
    }
}
