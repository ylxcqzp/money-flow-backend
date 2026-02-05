package com.ghost.moneyflowbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghost.moneyflowbackend.entity.BusCategory;
import com.ghost.moneyflowbackend.model.dto.CategoryCreateRequest;
import com.ghost.moneyflowbackend.model.dto.CategoryUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.CategoryVO;

import java.util.List;

/**
 * 收支分类业务服务
 */
public interface BusCategoryService extends IService<BusCategory> {
    List<CategoryVO> listCategories(String type);

    CategoryVO createCategory(CategoryCreateRequest request);

    CategoryVO updateCategory(Long categoryId, CategoryUpdateRequest request);

    void deleteCategory(Long categoryId);
}
