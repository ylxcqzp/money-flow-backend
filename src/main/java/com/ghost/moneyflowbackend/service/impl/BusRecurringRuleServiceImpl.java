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
import com.ghost.moneyflowbackend.model.dto.RecurringRuleCreateRequest;
import com.ghost.moneyflowbackend.model.dto.RecurringRuleUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.RecurringRuleVO;
import com.ghost.moneyflowbackend.model.vo.TransactionVO;
import com.ghost.moneyflowbackend.service.BusRecurringRuleService;
import com.ghost.moneyflowbackend.service.BusTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 周期性规则服务实现类
 * 
 * 该服务负责管理用户的周期性记账规则，包括：
 * 1. 创建、更新、删除周期性规则
 * 2. 查询用户的所有周期性规则
 * 3. 根据规则自动生成本日应执行的交易记录
 * 
 * 周期性规则允许用户设置定期发生的收支，如每月房租、每周工资等
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BusRecurringRuleServiceImpl extends ServiceImpl<BusRecurringRuleMapper, BusRecurringRule>
        implements BusRecurringRuleService {
    private final BusAccountMapper busAccountMapper;
    private final BusCategoryMapper busCategoryMapper;
    private final BusTransactionService busTransactionService;

    /**
     * 查询当前用户的所有周期性规则
     * 
     * @return 当前用户的所有周期性规则列表，按ID降序排列
     */
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

    /**
     * 创建新的周期性规则
     * 
     * @param request 创建周期性规则的请求参数
     * @return 创建成功的周期性规则视图对象
     * @throws BusinessException 参数校验失败或创建过程中出现异常时抛出
     */
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
        rule.setDescription(request.getDescription());
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

    /**
     * 更新指定的周期性规则
     * 
     * @param ruleId 要更新的规则ID
     * @param request 更新周期性规则的请求参数
     * @return 更新后的周期性规则视图对象
     * @throws BusinessException 参数校验失败、规则不存在或更新过程中出现异常时抛出
     */
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
        if (request.getDescription() != null) {
            rule.setDescription(request.getDescription());
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

    /**
     * 删除指定的周期性规则（软删除）
     * 
     * @param ruleId 要删除的规则ID
     * @throws BusinessException 规则不存在或删除过程中出现异常时抛出
     */
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

    /**
     * 根据周期性规则生成今日应执行的交易记录
     * 
     * 此方法会查找所有满足条件的周期性规则（当前用户、未删除、启用状态、到达执行日期），
     * 并为每条规则生成一条对应的交易记录，同时更新规则的下次执行日期
     * 
     * @return 生成的交易记录列表
     */
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
        return generateTransactionsByRules(rules);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<TransactionVO> generateAllTransactions() {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<BusRecurringRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusRecurringRule::getDelFlag, 0)
                .eq(BusRecurringRule::getStatus, 0)
                .le(BusRecurringRule::getNextExecutionDate, today);
        List<BusRecurringRule> rules = list(wrapper);
        return generateTransactionsByRules(rules);
    }

    /**
     * 根据周期性规则列表批量生成交易记录并更新规则执行状态
     *
     * @param rules 需要执行的周期性规则列表
     * @return 生成的交易记录VO列表
     * @throws BusinessException 如果生成交易或更新规则失败则抛出异常
     */
    private List<TransactionVO> generateTransactionsByRules(List<BusRecurringRule> rules) {
        // 1. 空校验：如果没有待处理规则，直接返回空列表
        if (rules.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 初始化缓存：减少循环内的重复数据库查询，提高批处理效率
        Map<String, BusCategory> categoryCache = new HashMap<>();
        Map<String, BusAccount> accountCache = new HashMap<>();

        // 3. 准备批量操作的数据容器
        List<BusTransaction> transactions = new ArrayList<>(rules.size());
        List<BusRecurringRule> updatedRules = new ArrayList<>(rules.size());

        // 4. 遍历规则，生成交易对象并计算下次执行时间
        for (BusRecurringRule rule : rules) {
            Long userId = rule.getUserId();

            // 获取并缓存分类信息 (key: userId + categoryId + type)
            String categoryKey = userId + ":" + rule.getCategoryId() + ":" + rule.getType();
            BusCategory category = categoryCache.computeIfAbsent(categoryKey,
                    key -> getCategory(userId, rule.getCategoryId(), rule.getType()));

            // 获取并缓存账户信息 (key: userId + accountId)
            String accountKey = userId + ":" + rule.getAccountId();
            BusAccount account = accountCache.computeIfAbsent(accountKey, key -> getAccount(userId, rule.getAccountId()));

            // 创建新的交易记录
            BusTransaction transaction = new BusTransaction();
            transaction.setUserId(userId);
            transaction.setType(rule.getType());
            transaction.setAmount(rule.getAmount());
            transaction.setDate(rule.getNextExecutionDate()); // 交易日期为规则设定的本次执行日期
            transaction.setCategoryId(category.getId());
            transaction.setAccountId(account.getId());
            transaction.setTargetAccountId(null);
            transaction.setNote(rule.getDescription()); // 使用规则的描述作为交易备注

            // 计算并更新该规则的下次执行日期
            LocalDate nextExecutionDate = calculateNextExecutionDate(rule.getNextExecutionDate(), rule.getFrequency());
            rule.setNextExecutionDate(nextExecutionDate);

            // 加入待保存/待更新队列
            transactions.add(transaction);
            updatedRules.add(rule);
        }

        // 5. 批量持久化交易记录 (每 200 条执行一次 SQL 以平衡内存和数据库压力)
        boolean saved = busTransactionService.saveBatch(transactions, 200);
        if (!saved) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "生成周期性交易失败");
        }

        // 6. 批量更新规则执行状态
        boolean updated = updateBatchById(updatedRules, 200);
        if (!updated) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "更新周期性规则失败");
        }

        // 7. 转换并返回 VO 列表
        List<TransactionVO> generatedTransactions = new ArrayList<>(transactions.size());
        for (BusTransaction transaction : transactions) {
            generatedTransactions.add(toTransactionVO(transaction));
        }
        return generatedTransactions;
    }

    /**
     * 根据ID获取指定的周期性规则，并进行权限校验
     * 
     * @param userId 当前登录用户ID
     * @param ruleId 要获取的规则ID
     * @return 周期性规则对象
     * @throws BusinessException 规则不存在或用户无权限访问时抛出
     */
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

    /**
     * 根据ID获取指定的账户，并进行权限校验
     * 
     * @param userId 当前登录用户ID
     * @param accountId 要获取的账户ID
     * @return 账户对象
     * @throws BusinessException 账户不存在或用户无权限使用该账户时抛出
     */
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

    /**
     * 根据ID获取指定的分类，并进行权限和类型校验
     * 
     * @param userId 当前登录用户ID
     * @param categoryId 要获取的分类ID
     * @param type 分类类型（支出/收入）
     * @return 分类对象
     * @throws BusinessException 分类不存在、用户无权限使用该分类或分类类型不匹配时抛出
     */
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

    /**
     * 校验交易类型参数
     * 
     * @param type 交易类型（expense-支出，income-收入）
     * @throws BusinessException 类型为空或不合法时抛出
     */
    private void validateType(String type) {
        if (!StringUtils.hasText(type)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "类型不能为空");
        }
        if (!"expense".equals(type) && !"income".equals(type)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "类型不合法");
        }
    }

    /**
     * 校验周期频率参数
     * 
     * @param frequency 周期频率（daily-每日，weekly-每周，monthly-每月，yearly-每年）
     * @throws BusinessException 频率为空或不合法时抛出
     */
    private void validateFrequency(String frequency) {
        if (!StringUtils.hasText(frequency)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "频率不能为空");
        }
        if (!"daily".equals(frequency) && !"weekly".equals(frequency) && !"monthly".equals(frequency)
                && !"yearly".equals(frequency)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "频率不合法");
        }
    }

    /**
     * 校验金额参数
     * 
     * @param amount 交易金额
     * @throws BusinessException 金额为空或小于等于0时抛出
     */
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "金额必须大于0");
        }
    }

    /**
     * 校验开始日期参数
     * 
     * @param startDate 开始日期
     * @throws BusinessException 开始日期为空时抛出
     */
    private void validateStartDate(LocalDate startDate) {
        if (startDate == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "开始日期不能为空");
        }
    }

    /**
     * 根据基础日期和频率计算下一次执行日期
     * 
     * @param baseDate 基础日期
     * @param frequency 执行频率（daily/weekly/monthly/yearly）
     * @return 下一次执行日期
     */
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

    /**
     * 将周期性规则实体转换为视图对象
     * 
     * @param rule 周期性规则实体对象
     * @return 周期性规则视图对象
     */
    private RecurringRuleVO toVO(BusRecurringRule rule) {
        RecurringRuleVO vo = new RecurringRuleVO();
        vo.setId(rule.getId());
        vo.setType(rule.getType());
        vo.setAmount(rule.getAmount());
        vo.setCategoryId(rule.getCategoryId());
        vo.setAccountId(rule.getAccountId());
        vo.setFrequency(rule.getFrequency());
        vo.setDescription(rule.getDescription());
        vo.setStartDate(rule.getStartDate());
        vo.setNextExecutionDate(rule.getNextExecutionDate());
        vo.setStatus(rule.getStatus());
        return vo;
    }

    /**
     * 将交易实体转换为视图对象
     * 
     * @param transaction 交易实体对象
     * @return 交易视图对象
     */
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
