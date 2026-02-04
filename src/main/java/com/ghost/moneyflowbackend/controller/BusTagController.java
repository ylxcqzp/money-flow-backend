package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.service.BusTagService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "标签管理")
@RestController
@RequestMapping("/api/tags")
public class BusTagController {

    private final BusTagService busTagService;

    public BusTagController(BusTagService busTagService) {
        this.busTagService = busTagService;
    }
}
