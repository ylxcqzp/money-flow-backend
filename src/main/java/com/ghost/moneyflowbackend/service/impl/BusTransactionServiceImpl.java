package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.common.exception.BusinessException;
import com.ghost.moneyflowbackend.common.exception.ErrorCode;
import com.ghost.moneyflowbackend.common.utils.SecurityUtils;
import com.ghost.moneyflowbackend.entity.BusAccount;
import com.ghost.moneyflowbackend.entity.BusCategory;
import com.ghost.moneyflowbackend.entity.BusTag;
import com.ghost.moneyflowbackend.entity.BusTransaction;
import com.ghost.moneyflowbackend.entity.BusTransactionTag;
import com.ghost.moneyflowbackend.mapper.BusAccountMapper;
import com.ghost.moneyflowbackend.mapper.BusCategoryMapper;
import com.ghost.moneyflowbackend.mapper.BusTagMapper;
import com.ghost.moneyflowbackend.mapper.BusTransactionMapper;
import com.ghost.moneyflowbackend.mapper.BusTransactionTagMapper;
import com.ghost.moneyflowbackend.model.dto.TransactionCreateRequest;
import com.ghost.moneyflowbackend.model.dto.TransactionUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.TransactionVO;
import com.ghost.moneyflowbackend.service.BusTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 交易明细业务服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BusTransactionServiceImpl extends ServiceImpl<BusTransactionMapper, BusTransaction>
        implements BusTransactionService {
    private final BusAccountMapper busAccountMapper;
    private final BusCategoryMapper busCategoryMapper;
    private final BusTransactionTagMapper busTransactionTagMapper;
    private final BusTagMapper busTagMapper;

    @Override
    public List<TransactionVO> listTransactions(LocalDate startDate, LocalDate endDate, String type,
            Long categoryId, Long accountId, List<String> tags) {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<BusTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusTransaction::getUserId, userId)
                .eq(BusTransaction::getDelFlag, 0);
        if (startDate != null) {
            wrapper.ge(BusTransaction::getDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(BusTransaction::getDate, endDate);
        }
        if (StringUtils.hasText(type) && !"all".equalsIgnoreCase(type)) {
            wrapper.eq(BusTransaction::getType, type);
        }
        if (categoryId != null) {
            wrapper.eq(BusTransaction::getCategoryId, categoryId);
        }
        if (accountId != null) {
            wrapper.eq(BusTransaction::getAccountId, accountId);
        }
        Set<Long> filterTransactionIds = null;
        if (!CollectionUtils.isEmpty(tags)) {
            Set<String> tagNames = tags.stream()
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toSet());
            if (tagNames.isEmpty()) {
                return Collections.emptyList();
            }
            LambdaQueryWrapper<BusTag> tagWrapper = new LambdaQueryWrapper<>();
            tagWrapper.eq(BusTag::getUserId, userId)
                    .eq(BusTag::getDelFlag, 0)
                    .in(BusTag::getName, tagNames);
            List<BusTag> tagList = busTagMapper.selectList(tagWrapper);
            if (tagList.isEmpty()) {
                return Collections.emptyList();
            }
            Set<Long> tagIds = tagList.stream().map(BusTag::getId).collect(Collectors.toSet());
            LambdaQueryWrapper<BusTransactionTag> linkWrapper = new LambdaQueryWrapper<>();
            linkWrapper.in(BusTransactionTag::getTagId, tagIds);
            List<BusTransactionTag> links = busTransactionTagMapper.selectList(linkWrapper);
            filterTransactionIds = links.stream().map(BusTransactionTag::getTransactionId).collect(Collectors.toSet());
            if (filterTransactionIds.isEmpty()) {
                return Collections.emptyList();
            }
            wrapper.in(BusTransaction::getId, filterTransactionIds);
        }
        wrapper.orderByDesc(BusTransaction::getDate, BusTransaction::getId);
        List<BusTransaction> transactions = list(wrapper);
        Map<Long, List<String>> tagMap = buildTagMap(transactions);
        List<TransactionVO> result = new ArrayList<>();
        for (BusTransaction transaction : transactions) {
            TransactionVO vo = toTransactionVO(transaction);
            vo.setTags(tagMap.getOrDefault(transaction.getId(), Collections.emptyList()));
            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionVO createTransaction(TransactionCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        validateType(request.getType());
        validateAmount(request.getAmount());
        validateDate(request.getDate());
        BusAccount account = getAccount(userId, request.getAccountId());
        Long targetAccountId = request.getTargetAccountId();
        String type = request.getType();
        if ("transfer".equals(type)) {
            if (targetAccountId == null) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "转账必须指定目标账户");
            }
            if (request.getAccountId().equals(targetAccountId)) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "转入账户不能与转出账户相同");
            }
            getAccount(userId, targetAccountId);
        } else {
            targetAccountId = null;
            if (request.getCategoryId() == null) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "分类不能为空");
            }
        }
        BusCategory category = null;
        if (!"transfer".equals(type)) {
            category = getCategory(userId, request.getCategoryId(), type);
        }
        BusTransaction transaction = new BusTransaction();
        transaction.setUserId(userId);
        transaction.setType(type);
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setOrigAmount(request.getOriginalAmount());
        transaction.setDate(request.getDate());
        transaction.setCategoryId(category == null ? null : category.getId());
        transaction.setAccountId(account.getId());
        transaction.setTargetAccountId(targetAccountId);
        transaction.setNote(request.getNote());
        boolean saved = save(transaction);
        if (!saved || transaction.getId() == null) {
            log.error("创建交易失败，用户ID: {}", userId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "创建交易失败");
        }
        updateTags(userId, transaction.getId(), request.getTags());
        TransactionVO vo = toTransactionVO(transaction);
        vo.setTags(loadTagNamesByTransactionId(transaction.getId()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransactionVO updateTransaction(Long transactionId, TransactionUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        BusTransaction transaction = getTransaction(userId, transactionId);
        if (request.getType() == null && request.getAmount() == null && request.getDate() == null
                && request.getCategoryId() == null && request.getAccountId() == null
                && request.getTargetAccountId() == null && request.getNote() == null && request.getTags() == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "至少更新一个字段");
        }
        String type = request.getType() != null ? request.getType() : transaction.getType();
        validateType(type);
        if (request.getAmount() != null) {
            validateAmount(request.getAmount());
            transaction.setAmount(request.getAmount());
        }
        if (request.getDate() != null) {
            validateDate(request.getDate());
            transaction.setDate(request.getDate());
        }
        Long accountId = request.getAccountId() != null ? request.getAccountId() : transaction.getAccountId();
        BusAccount account = getAccount(userId, accountId);
        transaction.setAccountId(account.getId());
        Long targetAccountId = request.getTargetAccountId() != null ? request.getTargetAccountId()
                : transaction.getTargetAccountId();
        if ("transfer".equals(type)) {
            if (targetAccountId == null) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "转账必须指定目标账户");
            }
            if (accountId.equals(targetAccountId)) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "转入账户不能与转出账户相同");
            }
            getAccount(userId, targetAccountId);
            transaction.setTargetAccountId(targetAccountId);
            transaction.setCategoryId(null);
        } else {
            Long categoryId = request.getCategoryId() != null ? request.getCategoryId() : transaction.getCategoryId();
            if (categoryId == null) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "分类不能为空");
            }
            BusCategory category = getCategory(userId, categoryId, type);
            transaction.setCategoryId(category.getId());
            transaction.setTargetAccountId(null);
        }
        if (request.getNote() != null) {
            transaction.setNote(request.getNote());
        }
        transaction.setType(type);
        boolean updated = updateById(transaction);
        if (!updated) {
            log.error("更新交易失败，用户ID: {}, 交易ID: {}", userId, transactionId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "更新交易失败");
        }
        if (request.getTags() != null) {
            updateTags(userId, transactionId, request.getTags());
        }
        TransactionVO vo = toTransactionVO(transaction);
        vo.setTags(loadTagNamesByTransactionId(transactionId));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTransaction(Long transactionId) {
        Long userId = SecurityUtils.getCurrentUserId();
        BusTransaction transaction = getTransaction(userId, transactionId);
        boolean removed = removeById(transaction.getId());
        if (!removed) {
            log.error("删除交易失败，用户ID: {}, 交易ID: {}", userId, transactionId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "删除交易失败");
        }
        LambdaQueryWrapper<BusTransactionTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusTransactionTag::getTransactionId, transactionId);
        busTransactionTagMapper.delete(wrapper);
    }

    private BusTransaction getTransaction(Long userId, Long transactionId) {
        BusTransaction transaction = getById(transactionId);
        if (transaction == null || transaction.getDelFlag() != null && transaction.getDelFlag() == 1) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "交易不存在");
        }
        if (!userId.equals(transaction.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限访问该交易");
        }
        return transaction;
    }

    private void validateType(String type) {
        if (!StringUtils.hasText(type)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "交易类型不能为空");
        }
        if (!"expense".equals(type) && !"income".equals(type) && !"transfer".equals(type)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "交易类型不合法");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "金额必须大于0");
        }
    }

    private void validateDate(LocalDate date) {
        if (date == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "发生时间不能为空");
        }
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

    private void updateTags(Long userId, Long transactionId, List<String> tags) {
        LambdaQueryWrapper<BusTransactionTag> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(BusTransactionTag::getTransactionId, transactionId);
        busTransactionTagMapper.delete(deleteWrapper);
        if (CollectionUtils.isEmpty(tags)) {
            return;
        }
        Set<String> tagNames = tags.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        if (tagNames.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<BusTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(BusTag::getUserId, userId)
                .in(BusTag::getName, tagNames);
        List<BusTag> existingTags = busTagMapper.selectList(tagWrapper);
        Map<String, BusTag> existingMap = new HashMap<>();
        for (BusTag tag : existingTags) {
            existingMap.put(tag.getName(), tag);
        }
        LocalDateTime now = LocalDateTime.now();
        Set<Long> tagIds = new HashSet<>();
        for (String name : tagNames) {
            BusTag tag = existingMap.get(name);
            if (tag == null) {
                BusTag newTag = new BusTag();
                newTag.setUserId(userId);
                newTag.setName(name);
                int inserted = busTagMapper.insert(newTag);
                if (inserted != 1 || newTag.getId() == null) {
                    log.error("创建标签失败，用户ID: {}, 标签名: {}", userId, name);
                    throw new BusinessException(ErrorCode.INTERNAL_ERROR, "创建标签失败");
                }
                tagIds.add(newTag.getId());
                continue;
            }
            if (tag.getDelFlag() != null && tag.getDelFlag() == 1) {
                tag.setDelFlag(0);
                busTagMapper.updateById(tag);
            }
            tagIds.add(tag.getId());
        }
        for (Long tagId : tagIds) {
            BusTransactionTag link = new BusTransactionTag();
            link.setTransactionId(transactionId);
            link.setTagId(tagId);
            busTransactionTagMapper.insert(link);
        }
    }

    private Map<Long, List<String>> buildTagMap(List<BusTransaction> transactions) {
        if (CollectionUtils.isEmpty(transactions)) {
            return Collections.emptyMap();
        }
        List<Long> transactionIds = transactions.stream().map(BusTransaction::getId).collect(Collectors.toList());
        LambdaQueryWrapper<BusTransactionTag> linkWrapper = new LambdaQueryWrapper<>();
        linkWrapper.in(BusTransactionTag::getTransactionId, transactionIds);
        List<BusTransactionTag> links = busTransactionTagMapper.selectList(linkWrapper);
        if (links.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> tagIds = links.stream().map(BusTransactionTag::getTagId).collect(Collectors.toSet());
        List<BusTag> tags = busTagMapper.selectBatchIds(tagIds);
        Map<Long, String> tagNameMap = tags.stream().collect(Collectors.toMap(BusTag::getId, BusTag::getName));
        Map<Long, List<String>> result = new LinkedHashMap<>();
        for (BusTransactionTag link : links) {
            String name = tagNameMap.get(link.getTagId());
            if (!StringUtils.hasText(name)) {
                continue;
            }
            result.computeIfAbsent(link.getTransactionId(), key -> new ArrayList<>()).add(name);
        }
        return result;
    }

    private List<String> loadTagNamesByTransactionId(Long transactionId) {
        LambdaQueryWrapper<BusTransactionTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusTransactionTag::getTransactionId, transactionId);
        List<BusTransactionTag> links = busTransactionTagMapper.selectList(wrapper);
        if (links.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> tagIds = links.stream().map(BusTransactionTag::getTagId).collect(Collectors.toSet());
        List<BusTag> tags = busTagMapper.selectBatchIds(tagIds);
        return tags.stream().map(BusTag::getName).filter(StringUtils::hasText).collect(Collectors.toList());
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
