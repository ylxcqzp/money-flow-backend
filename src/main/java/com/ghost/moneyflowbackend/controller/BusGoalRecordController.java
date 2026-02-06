package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.service.BusGoalRecordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "目标记录管理")
@RestController
@RequestMapping("/api/goal-records")
public class BusGoalRecordController {

    private final BusGoalRecordService busGoalRecordService;

    /**
     * 构造方法
     *
     * @param busGoalRecordService 目标记录业务服务
     */
    public BusGoalRecordController(BusGoalRecordService busGoalRecordService) {
        this.busGoalRecordService = busGoalRecordService;
    }
}
