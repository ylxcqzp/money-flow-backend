package com.ghost.moneyflowbackend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghost.moneyflowbackend.common.model.Result;
import com.ghost.moneyflowbackend.entity.BusTag;
import com.ghost.moneyflowbackend.service.BusTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 标签接口
 */
@Tag(name = "标签管理")
@Validated
@RestController
@RequestMapping("/api/tags")
public class BusTagController {

    private final BusTagService busTagService;

    /**
     * 标签接口构造方法
     *
     * @param busTagService 标签业务服务
     */
    public BusTagController(BusTagService busTagService) {
        this.busTagService = busTagService;
    }

    /**
     * 创建标签
     *
     * @param tag 标签信息
     * @return 创建结果
     */
    @Operation(summary = "创建标签")
    @PostMapping
    public Result<BusTag> create(@Valid @RequestBody BusTag tag) {
        busTagService.save(tag);
        return Result.ok(tag);
    }

    /**
     * 更新标签
     *
     * @param id 标签ID
     * @param tag 标签信息
     * @return 更新结果
     */
    @Operation(summary = "更新标签")
    @PutMapping("/{id}")
    public Result<BusTag> update(@PathVariable @NotNull Long id, @Valid @RequestBody BusTag tag) {
        tag.setId(id);
        busTagService.updateById(tag);
        return Result.ok(tag);
    }

    /**
     * 根据ID查询标签
     *
     * @param id 标签ID
     * @return 标签信息
     */
    @Operation(summary = "根据ID查询标签")
    @GetMapping("/{id}")
    public Result<BusTag> getById(@PathVariable @NotNull Long id) {
        return Result.ok(busTagService.getById(id));
    }

    /**
     * 分页查询标签
     *
     * @param page 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    @Operation(summary = "分页查询标签")
    @GetMapping("/page")
    public Result<IPage<BusTag>> page(@RequestParam(defaultValue = "1") @Min(1) long page,
                                      @RequestParam(defaultValue = "10") @Min(1) long size) {
        return Result.ok(busTagService.page(new Page<>(page, size)));
    }

    /**
     * 删除标签
     *
     * @param id 标签ID
     * @return 删除结果
     */
    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable @NotNull Long id) {
        return Result.ok(busTagService.removeById(id));
    }
}
