package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.common.exception.BusinessException;
import com.ghost.moneyflowbackend.common.exception.ErrorCode;
import com.ghost.moneyflowbackend.common.utils.SecurityUtils;
import com.ghost.moneyflowbackend.entity.BusBudget;
import com.ghost.moneyflowbackend.entity.BusBudgetItem;
import com.ghost.moneyflowbackend.entity.BusCategory;
import com.ghost.moneyflowbackend.mapper.BusBudgetMapper;
import com.ghost.moneyflowbackend.mapper.BusBudgetItemMapper;
import com.ghost.moneyflowbackend.mapper.BusCategoryMapper;
import com.ghost.moneyflowbackend.model.dto.BudgetSaveRequest;
import com.ghost.moneyflowbackend.model.vo.BudgetVO;
import com.ghost.moneyflowbackend.service.BusBudgetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预算业务服务实现
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BusBudgetServiceImpl extends ServiceImpl<BusBudgetMapper, BusBudget> implements BusBudgetService {
    private final BusBudgetItemMapper busBudgetItemMapper;
    private final BusCategoryMapper busCategoryMapper;

    /**
     * 获取指定月份预算信息
     *
     * @param month 月份（yyyy-MM）
     * @return 预算信息
     */
    @Override
    public BudgetVO getBudget(String month) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (!StringUtils.hasText(month)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "月份不能为空");
        }
        validateMonth(month);
        BusBudget budget = getBudgetEntity(userId, month);
        BudgetVO vo = new BudgetVO();
        vo.setMonth(month);
        if (budget == null) {
            vo.setTotal(BigDecimal.ZERO);
            vo.setCategories(new HashMap<>());
            return vo;
        }
        vo.setTotal(budget.getTotalAmount());
        vo.setCategories(loadBudgetItems(budget.getId()));
        return vo;
    }

    /**
     * 保存或更新预算信息
     *
     * @param request 预算保存参数
     * @return 保存后的预算信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BudgetVO saveBudget(BudgetSaveRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        String month = request.getMonth();
        if (!StringUtils.hasText(month)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "月份不能为空");
        }
        validateMonth(month);
        if (request.getTotal() == null || request.getTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "总预算金额不合法");
        }
        BusBudget budget = getBudgetEntity(userId, month);
        LocalDateTime now = LocalDateTime.now();
        if (budget == null) {
            budget = new BusBudget();
            budget.setUserId(userId);
            budget.setMonth(month);
            budget.setTotalAmount(request.getTotal());
            boolean saved = save(budget);
            if (!saved || budget.getId() == null) {
                log.error("创建预算失败，用户ID: {}, 月份: {}", userId, month);
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "保存预算失败");
            }
        } else {
            budget.setTotalAmount(request.getTotal());
            boolean updated = updateById(budget);
            if (!updated) {
                log.error("更新预算失败，用户ID: {}, 月份: {}", userId, month);
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "保存预算失败");
            }
        }
        upsertBudgetItems(userId, budget.getId(), request.getCategories());
        BudgetVO vo = new BudgetVO();
        vo.setMonth(month);
        vo.setTotal(budget.getTotalAmount());
        vo.setCategories(loadBudgetItems(budget.getId()));
        return vo;
    }

    /**
     * 获取预算实体对象
     *
     * @param userId 用户ID
     * @param month 月份（yyyy-MM）
     * @return 预算实体
     */
    private BusBudget getBudgetEntity(Long userId, String month) {
        LambdaQueryWrapper<BusBudget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusBudget::getUserId, userId)
                .eq(BusBudget::getMonth, month)
                .eq(BusBudget::getDelFlag, 0)
                .last("LIMIT 1");
        return getOne(wrapper, false);
    }

    /**
     * 加载预算明细到分类金额映射
     *
     * @param budgetId 预算ID
     * @return 分类预算金额映射
     */
    private Map<Long, BigDecimal> loadBudgetItems(Long budgetId) {
        LambdaQueryWrapper<BusBudgetItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusBudgetItem::getBudgetId, budgetId)
                .eq(BusBudgetItem::getDelFlag, 0);
        List<BusBudgetItem> items = busBudgetItemMapper.selectList(wrapper);
        Map<Long, BigDecimal> result = new HashMap<>();
        for (BusBudgetItem item : items) {
            result.put(item.getCategoryId(), item.getAmount());
        }
        return result;
    }

    /**
     * 保存或更新预算分类明细
     *
     * @param userId 用户ID
     * @param budgetId 预算ID
     * @param categories 分类预算映射
     */
    private void upsertBudgetItems(Long userId, Long budgetId, Map<Long, BigDecimal> categories) {
        LambdaQueryWrapper<BusBudgetItem> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(BusBudgetItem::getBudgetId, budgetId);
        busBudgetItemMapper.delete(deleteWrapper);
        if (categories == null || categories.isEmpty()) {
            return;
        }
        for (Map.Entry<Long, BigDecimal> entry : categories.entrySet()) {
            Long categoryId = entry.getKey();
            BigDecimal amount = entry.getValue();
            if (categoryId == null) {
                continue;
            }
            if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "分类预算金额不合法");
            }
            BusCategory category = busCategoryMapper.selectById(categoryId);
            if (category == null || category.getDelFlag() != null && category.getDelFlag() == 1) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "分类不存在");
            }
            if (category.getUserId() != null && !userId.equals(category.getUserId())) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "无权限使用该分类");
            }
            if (!"expense".equals(category.getType())) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "预算仅支持支出分类");
            }
            BusBudgetItem item = new BusBudgetItem();
            item.setBudgetId(budgetId);
            item.setCategoryId(categoryId);
            item.setAmount(amount);
            busBudgetItemMapper.insert(item);
        }
    }

    /**
     * 校验月份格式
     *
     * @param month 月份（yyyy-MM）
     */
    private void validateMonth(String month) {
        try {
            YearMonth.parse(month);
        } catch (DateTimeParseException ex) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "月份格式不正确");
        }
    }
}
