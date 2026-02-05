package com.ghost.moneyflowbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghost.moneyflowbackend.entity.BusAccount;
import com.ghost.moneyflowbackend.model.dto.AccountCreateRequest;
import com.ghost.moneyflowbackend.model.dto.AccountUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.AccountVO;

import java.util.List;

/**
 * 资产账户业务服务
 */
public interface BusAccountService extends IService<BusAccount> {
    List<AccountVO> listAccounts();

    AccountVO createAccount(AccountCreateRequest request);

    AccountVO updateAccount(Long accountId, AccountUpdateRequest request);

    void deleteAccount(Long accountId);
}
