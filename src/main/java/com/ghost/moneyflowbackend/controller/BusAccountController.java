package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.service.BusAccountService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "资产账户管理")
@RestController
@RequestMapping("/api/accounts")
public class BusAccountController {

    private final BusAccountService busAccountService;

    public BusAccountController(BusAccountService busAccountService) {
        this.busAccountService = busAccountService;
    }
}
