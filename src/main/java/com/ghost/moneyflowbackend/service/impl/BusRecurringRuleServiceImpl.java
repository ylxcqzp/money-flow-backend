package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.common.exception.BusinessException;
import com.ghost.moneyflowbackend.common.exception.ErrorCode;
import com.ghost.moneyflowbackend.common.utils.SecurityUtils;
import com.ghost.moneyflowbackend.entity.BusAccount;
import com.ghost.moneyflowbackend.entity.BusCategory;
import com.ghost.moneyflowbackend.entity.BusRecurringRule;
import com.ghost.moneyflowbackend.entity.BusTransaction;
import com.ghost.moneyflowbackend.mapper.BusAccountMapper;
import com.ghost.moneyflowbackend.mapper.BusCategoryMapper;
import com.ghost.moneyflowbackend.mapper.BusRecurringRuleMapper;
import com.ghost.moneyflowbackend.mapper.BusTransactionMapper;
import com.ghost.moneyflowbackend.model.dto.RecurringRuleCreateRequest;
import com.ghost.moneyflowbackend.model.dto.RecurringRuleUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.RecurringRuleVO;
import com.ghost.moneyflowbackend.model.vo.TransactionVO;
import com.ghost.moneyflowbackend.service.BusRecurringRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BusRecurringRuleServiceImpl extends ServiceImpl<BusRecurringRuleMapper, BusRecurringRule>
        implements BusRecurringRuleService {
    private final BusAccountMapper busAccountMapper;
    private final BusCategoryMapper busCategoryMapper;
    private final BusTransactionMapper busTransactionMapper;

    @Override
    public List<RecurringRuleVO> listRules() {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<BusRecurringRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusRecurringRule::getUserId, userId)
                .eq(BusRecurringRule::getDelFlag, 0)
                .orderByDesc(BusRecurringRule::getId);
        List<BusRecurringRule> rules = list(wrapper);
        List<RecurringRuleVO> result = new ArrayList<>();
        for (BusRecurringRule rule : rules) {
            result.add(toVO(rule));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecurringRuleVO createRule(RecurringRuleCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        validateType(request.getType());
        validateFrequency(request.getFrequency());
        validateAmount(request.getAmount());
        validateStartDate(request.getStartDate());
        BusCategory category = getCategory(userId, request.getCategoryId(), request.getType());
        BusAccount account = getAccount(userId, request.getAccountId());
        LocalDate nextExecutionDate = calculateNextExecutionDate(request.getStartDate(), request.getFrequency());
        BusRecurringRule rule = new BusRecurringRule();
        rule.setUserId(userId);
        rule.setType(request.getType());
        rule.setAmount(request.getAmount());
        rule.setCategoryId(category.getId());
        rule.setAccountId(account.getId());
        rule.setFrequency(request.getFrequency());
        rule.setStartDate(request.getStartDate());
        rule.setNextExecutionDate(nextExecutionDate);
        rule.setStatus(request.getStatus() == null ? 0 : request.getStatus());
        boolean saved = save(rule);
        if (!saved || rule.getId() == null) {
            log.error("创建周期性规则失败，用户ID: {}", userId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "创建规则失败");
        }
        return toVO(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecurringRuleVO updateRule(Long ruleId, RecurringRuleUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        BusRecurringRule rule = getRule(userId, ruleId);
        if (request.getType() == null && request.getAmount() == null && request.getFrequency() == null
                && request.getStartDate() == null && request.getCategoryId() == null && request.getAccountId() == null
                && request.getStatus() == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "至少更新一个字段");
        }
        String type = request.getType() != null ? request.getType() : rule.getType();
        validateType(type);
        if (request.getAmount() != null) {
            validateAmount(request.getAmount());
            rule.setAmount(request.getAmount());
        }
        if (request.getFrequency() != null) {
            validateFrequency(request.getFrequency());
            rule.setFrequency(request.getFrequency());
        }
        if (request.getStartDate() != null) {
            validateStartDate(request.getStartDate());
            rule.setStartDate(request.getStartDate());
        }
        Long categoryId = request.getCategoryId() != null ? request.getCategoryId() : rule.getCategoryId();
        BusCategory category = getCategory(userId, categoryId, type);
        rule.setCategoryId(category.getId());
        Long accountId = request.getAccountId() != null ? request.getAccountId() : rule.getAccountId();
        BusAccount account = getAccount(userId, accountId);
        rule.setAccountId(account.getId());
        if (request.getStatus() != null) {
            rule.setStatus(request.getStatus());
        }
        LocalDate nextExecutionDate = calculateNextExecutionDate(rule.getStartDate(), rule.getFrequency());
        rule.setNextExecutionDate(nextExecutionDate);
        rule.setType(type);
        boolean updated = updateById(rule);
        if (!updated) {
            log.error("更新周期性规则失败，用户ID: {}, 规则ID: {}", userId, ruleId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "更新规则失败");
        }
        return toVO(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRule(Long ruleId) {
        Long userId = SecurityUtils.getCurrentUserId();
        BusRecurringRule rule = getRule(userId, ruleId);
        boolean removed = removeById(rule.getId());
        if (!removed) {
            log.error("删除周期性规则失败，用户ID: {}, 规则ID: {}", userId, ruleId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "删除规则失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TransactionVO> generateTransactions() {
        Long userId = SecurityUtils.getCurrentUserId();
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<BusRecurringRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusRecurringRule::getUserId, userId)
                .eq(BusRecurringRule::getDelFlag, 0)
                .eq(BusRecurringRule::getStatus, 0)
                .le(BusRecurringRule::getNextExecutionDate, today);
        List<BusRecurringRule> rules = list(wrapper);
        if (rules.isEmpty()) {
            return Collections.emptyList();
        }
        List<TransactionVO> generatedTransactions = new ArrayList<>();
        for (BusRecurringRule rule : rules) {
            BusCategory category = getCategory(userId, rule.getCategoryId(), rule.getType());
            BusAccount account = getAccount(userId, rule.getAccountId());
            BusTransaction transaction = new BusTransaction();
            transaction.setUserId(userId);
            transaction.setType(rule.getType());
            transaction.setAmount(rule.getAmount());
            transaction.setDate(rule.getNextExecutionDate().atStartOfDay());
            transaction.setCategoryId(category.getId());
            transaction.setAccountId(account.getId());
            transaction.setTargetAccountId(null);
            transaction.setNote("周期性账单");
            int inserted = busTransactionMapper.insert(transaction);
            if (inserted == 1 && transaction.getId() != null) {
                generatedTransactions.add(toTransactionVO(transaction));
            }
            LocalDate nextExecutionDate = calculateNextExecutionDate(rule.getNextExecutionDate(), rule.getFrequency());
            rule.setNextExecutionDate(nextExecutionDate);
            updateById(rule);
        }
        return generatedTransactions;
    }

    private BusRecurringRule getRule(Long userId, Long ruleId) {
        BusRecurringRule rule = getById(ruleId);
        if (rule == null || rule.getDelFlag() != null && rule.getDelFlag() == 1) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "规则不存在");
        }
        if (!userId.equals(rule.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限访问该规则");
        }
        return rule;
    }

    private BusAccount getAccount(Long userId, Long accountId) {
        BusAccount account = busAccountMapper.selectById(accountId);
        if (account == null || account.getDelFlag() != null && account.getDelFlag() == 1) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "账户不存在");
        }
        if (!userId.equals(account.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限使用该账户");
        }
        return account;
    }

    private BusCategory getCategory(Long userId, Long categoryId, String type) {
        BusCategory category = busCategoryMapper.selectById(categoryId);
        if (category == null || category.getDelFlag() != null && category.getDelFlag() == 1) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "分类不存在");
        }
        if (category.getUserId() != null && !userId.equals(category.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限使用该分类");
        }
        if (!type.equals(category.getType())) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "分类类型不匹配");
        }
        return category;
    }

    private void validateType(String type) {
        if (!StringUtils.hasText(type)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "类型不能为空");
        }
        if (!"expense".equals(type) && !"income".equals(type)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "类型不合法");
        }
    }

    private void validateFrequency(String frequency) {
        if (!StringUtils.hasText(frequency)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "频率不能为空");
        }
        if (!"daily".equals(frequency) && !"weekly".equals(frequency) && !"monthly".equals(frequency)
                && !"yearly".equals(frequency)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "频率不合法");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "金额必须大于0");
        }
    }

    private void validateStartDate(LocalDate startDate) {
        if (startDate == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "开始日期不能为空");
        }
    }

    private LocalDate calculateNextExecutionDate(LocalDate baseDate, String frequency) {
        if ("daily".equals(frequency)) {
            return baseDate.plusDays(1);
        }
        if ("weekly".equals(frequency)) {
            return baseDate.plusWeeks(1);
        }
        if ("monthly".equals(frequency)) {
            return baseDate.plusMonths(1);
        }
        return baseDate.plusYears(1);
    }

    private RecurringRuleVO toVO(BusRecurringRule rule) {
        RecurringRuleVO vo = new RecurringRuleVO();
        vo.setId(rule.getId());
        vo.setType(rule.getType());
        vo.setAmount(rule.getAmount());
        vo.setCategoryId(rule.getCategoryId());
        vo.setAccountId(rule.getAccountId());
        vo.setFrequency(rule.getFrequency());
        vo.setStartDate(rule.getStartDate());
        vo.setNextExecutionDate(rule.getNextExecutionDate());
        vo.setStatus(rule.getStatus());
        return vo;
    }

    private TransactionVO toTransactionVO(BusTransaction transaction) {
        TransactionVO vo = new TransactionVO();
        vo.setId(transaction.getId());
        vo.setType(transaction.getType());
        vo.setAmount(transaction.getAmount());
        vo.setDate(transaction.getDate());
        vo.setCategoryId(transaction.getCategoryId());
        vo.setAccountId(transaction.getAccountId());
        vo.setTargetAccountId(transaction.getTargetAccountId());
        vo.setNote(transaction.getNote());
        return vo;
    }
}
