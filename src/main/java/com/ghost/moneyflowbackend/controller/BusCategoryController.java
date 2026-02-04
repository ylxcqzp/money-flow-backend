package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.service.BusCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "收支分类管理")
@RestController
@RequestMapping("/api/categories")
public class BusCategoryController {

    private final BusCategoryService busCategoryService;

    public BusCategoryController(BusCategoryService busCategoryService) {
        this.busCategoryService = busCategoryService;
    }
}
