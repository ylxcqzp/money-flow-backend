package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.common.exception.BusinessException;
import com.ghost.moneyflowbackend.common.exception.ErrorCode;
import com.ghost.moneyflowbackend.common.utils.SecurityUtils;
import com.ghost.moneyflowbackend.entity.BusCategory;
import com.ghost.moneyflowbackend.mapper.BusCategoryMapper;
import com.ghost.moneyflowbackend.mapper.BusTransactionMapper;
import com.ghost.moneyflowbackend.model.dto.CategoryCreateRequest;
import com.ghost.moneyflowbackend.model.dto.CategoryUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.CategoryVO;
import com.ghost.moneyflowbackend.service.BusCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 收支分类业务服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusCategoryServiceImpl extends ServiceImpl<BusCategoryMapper, BusCategory> implements BusCategoryService {
    private final BusTransactionMapper busTransactionMapper;

    /**
     * 获取分类列表，并构建父子层级结构
     *
     * @param type 分类类型（可选）
     * @return 分类树列表
     */
    @Override
    public List<CategoryVO> listCategories(String type) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<BusCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusCategory::getDelFlag, 0)
                .and(query -> query.eq(BusCategory::getUserId, userId).or().isNull(BusCategory::getUserId));
        if (StringUtils.hasText(type)) {
            wrapper.eq(BusCategory::getType, type);
        }
        wrapper.orderByAsc(BusCategory::getSortOrder, BusCategory::getId);
        List<BusCategory> categories = list(wrapper);
        Map<Long, CategoryVO> categoryMap = new LinkedHashMap<>();
        for (BusCategory category : categories) {
            CategoryVO vo = toCategoryVO(category);
            categoryMap.put(vo.getId(), vo);
        }
        List<CategoryVO> roots = new ArrayList<>();
        for (CategoryVO category : categoryMap.values()) {
            Long parentId = category.getParentId();
            if (parentId == null || parentId == 0) {
                roots.add(category);
                continue;
            }
            CategoryVO parent = categoryMap.get(parentId);
            if (parent == null) {
                roots.add(category);
                continue;
            }
            parent.getChildren().add(category);
        }
        return roots;
    }

    /**
     * 创建分类
     *
     * @param request 创建参数
     * @return 创建后的分类信息
     */
    @Override
    public CategoryVO createCategory(CategoryCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (request.getParentId() != null && request.getParentId() != 0) {
            BusCategory parent = getById(request.getParentId());
            if (parent == null || parent.getDelFlag() != null && parent.getDelFlag() == 1) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "父级分类不存在");
            }
            if (parent.getUserId() != null && !userId.equals(parent.getUserId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "无权限使用该父级分类");
            }
        }
        BusCategory category = new BusCategory();
        category.setUserId(userId);
        category.setName(request.getName());
        category.setType(request.getType());
        category.setIcon(request.getIcon());
        category.setParentId(request.getParentId());
        category.setSortOrder(request.getSortOrder());
        boolean saved = save(category);
        if (!saved || category.getId() == null) {
            log.error("创建分类失败，用户ID: {}", userId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "创建分类失败");
        }
        return toCategoryVO(category);
    }

    /**
     * 更新分类
     *
     * @param categoryId 分类ID
     * @param request 更新参数
     * @return 更新后的分类信息
     */
    @Override
    public CategoryVO updateCategory(Long categoryId, CategoryUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        BusCategory category = getUserCategory(userId, categoryId);
        if (request.getName() == null && request.getType() == null && request.getIcon() == null
                && request.getParentId() == null && request.getSortOrder() == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "至少更新一个字段");
        }
        if (request.getParentId() != null && request.getParentId() != 0) {
            if (categoryId.equals(request.getParentId())) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "父级分类不能为自身");
            }
            BusCategory parent = getById(request.getParentId());
            if (parent == null || parent.getDelFlag() != null && parent.getDelFlag() == 1) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "父级分类不存在");
            }
            if (parent.getUserId() != null && !userId.equals(parent.getUserId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "无权限使用该父级分类");
            }
        }
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getType() != null) {
            category.setType(request.getType());
        }
        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }
        if (request.getParentId() != null) {
            category.setParentId(request.getParentId());
        }
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }
        boolean updated = updateById(category);
        if (!updated) {
            log.error("更新分类失败，用户ID: {}, 分类ID: {}", userId, categoryId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "更新分类失败");
        }
        return toCategoryVO(category);
    }

    /**
     * 删除分类
     *
     * @param categoryId 分类ID
     */
    @Override
    public void deleteCategory(Long categoryId) {
        Long userId = SecurityUtils.getCurrentUserId();
        BusCategory category = getUserCategory(userId, categoryId);
        LambdaQueryWrapper<BusCategory> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(BusCategory::getParentId, categoryId)
                .eq(BusCategory::getDelFlag, 0)
                .eq(BusCategory::getUserId, userId);
        if (count(childWrapper) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "请先删除子分类");
        }
        if (busTransactionMapper.countByCategory(userId, categoryId) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "分类已关联交易，无法删除");
        }
        boolean removed = removeById(category.getId());
        if (!removed) {
            log.error("删除分类失败，用户ID: {}, 分类ID: {}", userId, categoryId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "删除分类失败");
        }
    }

    /**
     * 获取用户自定义分类并校验权限
     *
     * @param userId 用户ID
     * @param categoryId 分类ID
     * @return 分类实体
     */
    private BusCategory getUserCategory(Long userId, Long categoryId) {
        BusCategory category = getById(categoryId);
        if (category == null || category.getDelFlag() != null && category.getDelFlag() == 1) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "分类不存在");
        }
        if (category.getUserId() == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "系统分类不允许修改");
        }
        if (!userId.equals(category.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限访问该分类");
        }
        return category;
    }

    /**
     * 转换分类实体为视图对象
     *
     * @param category 分类实体
     * @return 分类视图对象
     */
    private CategoryVO toCategoryVO(BusCategory category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setType(category.getType());
        vo.setIcon(category.getIcon());
        vo.setParentId(category.getParentId());
        vo.setSortOrder(category.getSortOrder());
        return vo;
    }
}
