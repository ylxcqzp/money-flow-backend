package com.ghost.moneyflowbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghost.moneyflowbackend.entity.BusTransaction;
import com.ghost.moneyflowbackend.model.dto.TransactionCreateRequest;
import com.ghost.moneyflowbackend.model.dto.TransactionUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.TransactionVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易明细业务服务
 */
public interface BusTransactionService extends IService<BusTransaction> {
    List<TransactionVO> listTransactions(LocalDateTime startDate, LocalDateTime endDate, String type,
                                         Long categoryId, Long accountId, List<String> tags);

    TransactionVO createTransaction(TransactionCreateRequest request);

    TransactionVO updateTransaction(Long transactionId, TransactionUpdateRequest request);

    void deleteTransaction(Long transactionId);
}
