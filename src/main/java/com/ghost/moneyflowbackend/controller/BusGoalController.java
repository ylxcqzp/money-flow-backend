package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.service.BusGoalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "储蓄目标管理")
@RestController
@RequestMapping("/api/goals")
public class BusGoalController {

    private final BusGoalService busGoalService;

    public BusGoalController(BusGoalService busGoalService) {
        this.busGoalService = busGoalService;
    }
}
