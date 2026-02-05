package com.ghost.moneyflowbackend.controller;

import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.model.dto.CategoryCreateRequest;
import com.ghost.moneyflowbackend.model.dto.CategoryUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.CategoryVO;
import com.ghost.moneyflowbackend.service.BusCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "收支分类管理")
@Validated
@RestController
@RequestMapping("/api/categories")
public class BusCategoryController {

    private final BusCategoryService busCategoryService;

    public BusCategoryController(BusCategoryService busCategoryService) {
        this.busCategoryService = busCategoryService;
    }

    @Operation(summary = "获取分类列表")
    @GetMapping
    public Result<List<CategoryVO>> list(@RequestParam(value = "type", required = false) String type) {
        return Result.ok(busCategoryService.listCategories(type));
    }

    @Operation(summary = "创建分类")
    @PostMapping
    public Result<CategoryVO> create(@Valid @RequestBody CategoryCreateRequest request) {
        return Result.ok(busCategoryService.createCategory(request));
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<CategoryVO> update(@PathVariable("id") @NotNull(message = "分类ID不能为空") Long id,
                                     @RequestBody CategoryUpdateRequest request) {
        return Result.ok(busCategoryService.updateCategory(id, request));
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") @NotNull(message = "分类ID不能为空") Long id) {
        busCategoryService.deleteCategory(id);
        return Result.ok(null);
    }
}
