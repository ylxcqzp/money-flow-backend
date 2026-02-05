package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.common.exception.BusinessException;
import com.ghost.moneyflowbackend.common.exception.ErrorCode;
import com.ghost.moneyflowbackend.common.utils.SecurityUtils;
import com.ghost.moneyflowbackend.entity.BusAccount;
import com.ghost.moneyflowbackend.mapper.BusAccountMapper;
import com.ghost.moneyflowbackend.mapper.BusTransactionMapper;
import com.ghost.moneyflowbackend.model.dto.AccountCreateRequest;
import com.ghost.moneyflowbackend.model.dto.AccountUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.AccountVO;
import com.ghost.moneyflowbackend.service.BusAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 资产账户业务服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusAccountServiceImpl extends ServiceImpl<BusAccountMapper, BusAccount> implements BusAccountService {
    private final BusTransactionMapper busTransactionMapper;

    @Override
    public List<AccountVO> listAccounts() {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<BusAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusAccount::getUserId, userId)
                .eq(BusAccount::getDelFlag, 0)
                .orderByAsc(BusAccount::getSortOrder, BusAccount::getId);
        List<BusAccount> accounts = list(wrapper);
        List<AccountVO> result = new ArrayList<>();
        for (BusAccount account : accounts) {
            AccountVO vo = toAccountVO(account);
            vo.setCurrentBalance(calculateCurrentBalance(account));
            result.add(vo);
        }
        return result;
    }

    @Override
    public AccountVO createAccount(AccountCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        BusAccount account = new BusAccount();
        account.setUserId(userId);
        account.setName(request.getName());
        account.setType(request.getType());
        account.setIcon(request.getIcon());
        account.setInitialBalance(request.getInitialBalance());
        account.setSortOrder(request.getSortOrder());
        account.setDelFlag(0);
        account.setCreateBy(userId);
        account.setCreateTime(LocalDateTime.now());
        account.setUpdateBy(userId);
        account.setUpdateTime(LocalDateTime.now());
        boolean saved = save(account);
        if (!saved || account.getId() == null) {
            log.error("创建账户失败，用户ID: {}", userId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "创建账户失败");
        }
        AccountVO vo = toAccountVO(account);
        vo.setCurrentBalance(account.getInitialBalance());
        return vo;
    }

    @Override
    public AccountVO updateAccount(Long accountId, AccountUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        BusAccount account = getAccountById(userId, accountId);
        if (request.getName() == null && request.getType() == null && request.getIcon() == null
                && request.getInitialBalance() == null && request.getSortOrder() == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "至少更新一个字段");
        }
        if (request.getName() != null) {
            account.setName(request.getName());
        }
        if (request.getType() != null) {
            account.setType(request.getType());
        }
        if (request.getIcon() != null) {
            account.setIcon(request.getIcon());
        }
        if (request.getInitialBalance() != null) {
            account.setInitialBalance(request.getInitialBalance());
        }
        if (request.getSortOrder() != null) {
            account.setSortOrder(request.getSortOrder());
        }
        account.setUpdateBy(userId);
        account.setUpdateTime(LocalDateTime.now());
        boolean updated = updateById(account);
        if (!updated) {
            log.error("更新账户失败，用户ID: {}, 账户ID: {}", userId, accountId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "更新账户失败");
        }
        AccountVO vo = toAccountVO(account);
        vo.setCurrentBalance(calculateCurrentBalance(account));
        return vo;
    }

    @Override
    public void deleteAccount(Long accountId) {
        Long userId = SecurityUtils.getCurrentUserId();
        BusAccount account = getAccountById(userId, accountId);
        if (account.getIsSystem() != null && account.getIsSystem() == 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "系统默认账户不允许删除");
        }
        if (busTransactionMapper.countByAccount(userId, accountId) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "账户已关联交易，无法删除");
        }
        boolean removed = removeById(account.getId());
        if (!removed) {
            log.error("删除账户失败，用户ID: {}, 账户ID: {}", userId, accountId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "删除账户失败");
        }
    }

    private BusAccount getAccountById(Long userId, Long accountId) {
        BusAccount account = getById(accountId);
        if (account == null || account.getDelFlag() != null && account.getDelFlag() == 1) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "账户不存在");
        }
        if (!userId.equals(account.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限访问该账户");
        }
        return account;
    }

    private BigDecimal calculateCurrentBalance(BusAccount account) {
        Long userId = account.getUserId();
        Long accountId = account.getId();
        BigDecimal income = nullSafe(busTransactionMapper.sumIncome(userId, accountId));
        BigDecimal expense = nullSafe(busTransactionMapper.sumExpense(userId, accountId));
        BigDecimal transferOut = nullSafe(busTransactionMapper.sumTransferOut(userId, accountId));
        BigDecimal transferIn = nullSafe(busTransactionMapper.sumTransferIn(userId, accountId));
        BigDecimal initial = nullSafe(account.getInitialBalance());
        return initial.add(income).add(transferIn).subtract(expense).subtract(transferOut);
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private AccountVO toAccountVO(BusAccount account) {
        AccountVO vo = new AccountVO();
        vo.setId(account.getId());
        vo.setName(account.getName());
        vo.setType(account.getType());
        vo.setIcon(account.getIcon());
        vo.setInitialBalance(account.getInitialBalance());
        vo.setSortOrder(account.getSortOrder());
        return vo;
    }
}
