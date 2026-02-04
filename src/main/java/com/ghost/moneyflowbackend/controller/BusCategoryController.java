package com.ghost.moneyflowbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.entity.BusCategory;
import com.ghost.moneyflowbackend.service.BusCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 收支分类接口
 */
@Tag(name = "收支分类管理")
@Validated
@RestController
@RequestMapping("/api/categories")
public class BusCategoryController {

    private final BusCategoryService busCategoryService;

    /**
     * 收支分类接口构造方法
     *
     * @param busCategoryService 收支分类业务服务
     */
    public BusCategoryController(BusCategoryService busCategoryService) {
        this.busCategoryService = busCategoryService;
    }

    /**
     * 创建分类
     *
     * @param category 分类信息
     * @return 创建结果
     */
    @Operation(summary = "创建分类")
    @PostMapping
    public Result<BusCategory> create(@Valid @RequestBody BusCategory category) {
        busCategoryService.save(category);
        return Result.ok(category);
    }

    /**
     * 更新分类
     *
     * @param id 分类ID
     * @param category 分类信息
     * @return 更新结果
     */
    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<BusCategory> update(@PathVariable @NotNull Long id, @Valid @RequestBody BusCategory category) {
        category.setId(id);
        busCategoryService.updateById(category);
        return Result.ok(category);
    }

    /**
     * 根据ID查询分类
     *
     * @param id 分类ID
     * @return 分类信息
     */
    @Operation(summary = "根据ID查询分类")
    @GetMapping("/{id}")
    public Result<BusCategory> getById(@PathVariable @NotNull Long id) {
        return Result.ok(busCategoryService.getById(id));
    }

    /**
     * 分页查询分类
     *
     * @param page 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询分类")
    @GetMapping("/page")
    public Result<IPage<BusCategory>> page(@RequestParam(defaultValue = "1") @Min(1) long page,
                                           @RequestParam(defaultValue = "10") @Min(1) long size) {
        return Result.ok(busCategoryService.page(new Page<>(page, size)));
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 删除结果
     */
    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.ok(busCategoryService.removeById(id));
    }
}
