package com.ghost.moneyflowbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ghost.moneyflowbackend.entity.BusAccount;
import com.ghost.moneyflowbackend.entity.BusCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认数据初始化服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDataInitializer {

    private final BusAccountService busAccountService;

    private final BusCategoryService busCategoryService;

    /**
     * 为新用户初始化默认数据
     *
     * @param userId 用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void initializeDefaultData(Long userId) {
        log.info("开始为用户初始化默认数据，用户ID: {}", userId);
        initializeDefaultAccounts(userId);
        initializeDefaultCategories(userId);
        log.info("默认数据初始化完成，用户ID: {}", userId);
    }

    /**
     * 初始化默认账户
     *
     * @param userId 用户ID
     */
    private void initializeDefaultAccounts(Long userId) {
        List<BusAccount> defaultAccounts = new ArrayList<>();
        defaultAccounts.add(createDefaultAccount(userId, "现金", "cash", "wallet", 1));
        defaultAccounts.add(createDefaultAccount(userId, "银行卡", "card", "credit-card", 2));
        defaultAccounts.add(createDefaultAccount(userId, "支付宝", "alipay", "alipay", 3));
        defaultAccounts.add(createDefaultAccount(userId, "微信", "wechat", "wechat", 4));
        for (BusAccount account : defaultAccounts) {
            busAccountService.save(account);
        }
        log.info("默认账户初始化完成，用户ID: {}, 账户数量: {}", userId, defaultAccounts.size());
    }

    /**
     * 创建默认账户实体
     *
     * @param userId 用户ID
     * @param name   账户名称
     * @param type   账户类型
     * @param icon   图标
     * @param order  排序
     * @return 账户实体
     */
    private BusAccount createDefaultAccount(Long userId, String name, String type, String icon, int order) {
        BusAccount account = new BusAccount();
        account.setUserId(userId);
        account.setName(name);
        account.setType(type);
        account.setIcon(icon);
        account.setInitialBalance(BigDecimal.ZERO);
        account.setSortOrder(order);
        account.setIsSystem(1);
        account.setDelFlag(0);
        account.setCreateBy(userId);
        return account;
    }

    /**
     * 初始化默认分类
     *
     * @param userId 用户ID
     */
    private void initializeDefaultCategories(Long userId) {
        List<BusCategory> defaultCategories = new ArrayList<>();
        defaultCategories.add(createDefaultCategory(userId, "餐饮", "expense", "restaurant", 1));
        defaultCategories.add(createDefaultCategory(userId, "交通", "expense", "car", 2));
        defaultCategories.add(createDefaultCategory(userId, "购物", "expense", "shopping-bag", 3));
        defaultCategories.add(createDefaultCategory(userId, "娱乐", "expense", "game-controller", 4));
        defaultCategories.add(createDefaultCategory(userId, "医疗", "expense", "medical", 5));
        defaultCategories.add(createDefaultCategory(userId, "教育", "expense", "book", 6));
        defaultCategories.add(createDefaultCategory(userId, "居住", "expense", "home", 7));
        defaultCategories.add(createDefaultCategory(userId, "其他支出", "expense", "more", 8));
        defaultCategories.add(createDefaultCategory(userId, "工资", "income", "wallet", 1));
        defaultCategories.add(createDefaultCategory(userId, "奖金", "income", "gift", 2));
        defaultCategories.add(createDefaultCategory(userId, "投资", "income", "trending-up", 3));
        defaultCategories.add(createDefaultCategory(userId, "兼职", "income", "briefcase", 4));
        defaultCategories.add(createDefaultCategory(userId, "其他收入", "income", "more", 5));
        for (BusCategory category : defaultCategories) {
            busCategoryService.save(category);
        }
        log.info("默认分类初始化完成，用户ID: {}, 分类数量: {}", userId, defaultCategories.size());
    }

    /**
     * 创建默认分类实体
     *
     * @param userId 用户ID
     * @param name   分类名称
     * @param type   分类类型
     * @param icon   图标
     * @param order  排序
     * @return 分类实体
     */
    private BusCategory createDefaultCategory(Long userId, String name, String type, String icon, int order) {
        BusCategory category = new BusCategory();
        category.setUserId(userId);
        category.setName(name);
        category.setType(type);
        category.setIcon(icon);
        category.setParentId(null);
        category.setSortOrder(order);
        category.setDelFlag(0);
        category.setCreateBy(userId);
        return category;
    }
}
