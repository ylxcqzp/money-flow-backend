package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghost.moneyflowbackend.common.exception.BusinessException;
import com.ghost.moneyflowbackend.common.exception.ErrorCode;
import com.ghost.moneyflowbackend.common.utils.SecurityUtils;
import com.ghost.moneyflowbackend.entity.BusGoal;
import com.ghost.moneyflowbackend.entity.BusGoalRecord;
import com.ghost.moneyflowbackend.mapper.BusGoalMapper;
import com.ghost.moneyflowbackend.mapper.BusGoalRecordMapper;
import com.ghost.moneyflowbackend.model.dto.GoalCreateRequest;
import com.ghost.moneyflowbackend.model.dto.GoalRecordCreateRequest;
import com.ghost.moneyflowbackend.model.dto.GoalUpdateRequest;
import com.ghost.moneyflowbackend.model.vo.GoalVO;
import com.ghost.moneyflowbackend.service.BusGoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class BusGoalServiceImpl extends ServiceImpl<BusGoalMapper, BusGoal> implements BusGoalService {
    private static final Set<String> ALLOWED_STATUSES = Set.of("ongoing", "completed", "archived");

    private final BusGoalRecordMapper busGoalRecordMapper;

    @Override
    public List<GoalVO> listGoals() {
        Long userId = SecurityUtils.getCurrentUserId();
        LambdaQueryWrapper<BusGoal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BusGoal::getUserId, userId)
                .eq(BusGoal::getDelFlag, 0)
                .orderByDesc(BusGoal::getId);
        List<BusGoal> goals = list(wrapper);
        List<GoalVO> result = new ArrayList<>();
        for (BusGoal goal : goals) {
            result.add(toVO(goal));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GoalVO createGoal(GoalCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        validateName(request.getName());
        validateTargetAmount(request.getTargetAmount());
        BusGoal goal = new BusGoal();
        goal.setUserId(userId);
        goal.setName(request.getName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setDeadline(request.getDeadline());
        goal.setIcon(request.getIcon());
        goal.setColor(request.getColor());
        goal.setStatus("ongoing");
        goal.setDelFlag(0);
        goal.setCreateBy(userId);
        goal.setCreateTime(LocalDateTime.now());
        goal.setUpdateBy(userId);
        goal.setUpdateTime(LocalDateTime.now());
        boolean saved = save(goal);
        if (!saved || goal.getId() == null) {
            log.error("创建目标失败，用户ID: {}", userId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "创建目标失败");
        }
        return toVO(goal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GoalVO updateGoal(Long goalId, GoalUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        BusGoal goal = getGoal(userId, goalId);
        if (request.getName() == null && request.getTargetAmount() == null && request.getDeadline() == null
                && request.getIcon() == null && request.getColor() == null && request.getStatus() == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "至少更新一个字段");
        }
        if (request.getName() != null) {
            validateName(request.getName());
            goal.setName(request.getName());
        }
        if (request.getTargetAmount() != null) {
            validateTargetAmount(request.getTargetAmount());
            BigDecimal currentAmount = goal.getCurrentAmount() == null ? BigDecimal.ZERO : goal.getCurrentAmount();
            if (request.getTargetAmount().compareTo(currentAmount) < 0) {
                throw new BusinessException(ErrorCode.INVALID_PARAM, "目标金额不能小于当前金额");
            }
            goal.setTargetAmount(request.getTargetAmount());
        }
        if (request.getDeadline() != null) {
            goal.setDeadline(request.getDeadline());
        }
        if (request.getIcon() != null) {
            goal.setIcon(request.getIcon());
        }
        if (request.getColor() != null) {
            goal.setColor(request.getColor());
        }
        if (request.getStatus() != null) {
            validateStatus(request.getStatus());
            goal.setStatus(request.getStatus());
        }
        goal.setUpdateBy(userId);
        goal.setUpdateTime(LocalDateTime.now());
        boolean updated = updateById(goal);
        if (!updated) {
            log.error("更新目标失败，用户ID: {}, 目标ID: {}", userId, goalId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "更新目标失败");
        }
        return toVO(goal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGoal(Long goalId) {
        Long userId = SecurityUtils.getCurrentUserId();
        BusGoal goal = getGoal(userId, goalId);
        boolean removed = removeById(goal.getId());
        if (!removed) {
            log.error("删除目标失败，用户ID: {}, 目标ID: {}", userId, goalId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "删除目标失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GoalVO createRecord(Long goalId, GoalRecordCreateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        BusGoal goal = getGoal(userId, goalId);
        if ("archived".equals(goal.getStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "目标已归档");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "操作金额不能为0");
        }
        BigDecimal currentAmount = goal.getCurrentAmount() == null ? BigDecimal.ZERO : goal.getCurrentAmount();
        BigDecimal newAmount = currentAmount.add(request.getAmount());
        if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "取出金额超过当前余额");
        }
        goal.setCurrentAmount(newAmount);
        if (goal.getTargetAmount() != null && newAmount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus("completed");
        }
        goal.setUpdateBy(userId);
        goal.setUpdateTime(LocalDateTime.now());
        boolean updated = updateById(goal);
        if (!updated) {
            log.error("更新目标金额失败，用户ID: {}, 目标ID: {}", userId, goalId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "更新目标失败");
        }
        BusGoalRecord record = new BusGoalRecord();
        record.setGoalId(goal.getId());
        record.setAmount(request.getAmount());
        LocalDate operateDate = request.getOperateDate() == null ? LocalDate.now() : request.getOperateDate();
        record.setOperateDate(operateDate);
        record.setCreateBy(userId);
        record.setCreateTime(LocalDateTime.now());
        int inserted = busGoalRecordMapper.insert(record);
        if (inserted != 1 || record.getId() == null) {
            log.error("创建目标记录失败，用户ID: {}, 目标ID: {}", userId, goalId);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "创建目标记录失败");
        }
        return toVO(goal);
    }

    private BusGoal getGoal(Long userId, Long goalId) {
        BusGoal goal = getById(goalId);
        if (goal == null || goal.getDelFlag() != null && goal.getDelFlag() == 1) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "目标不存在");
        }
        if (!userId.equals(goal.getUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限访问该目标");
        }
        return goal;
    }

    private void validateName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "目标名称不能为空");
        }
    }

    private void validateTargetAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "目标金额必须大于0");
        }
    }

    private void validateStatus(String status) {
        if (!StringUtils.hasText(status) || !ALLOWED_STATUSES.contains(status)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "目标状态不合法");
        }
    }

    private GoalVO toVO(BusGoal goal) {
        GoalVO vo = new GoalVO();
        vo.setId(goal.getId());
        vo.setName(goal.getName());
        vo.setTargetAmount(goal.getTargetAmount());
        vo.setCurrentAmount(goal.getCurrentAmount() == null ? BigDecimal.ZERO : goal.getCurrentAmount());
        vo.setDeadline(goal.getDeadline());
        vo.setIcon(goal.getIcon());
        vo.setColor(goal.getColor());
        vo.setStatus(goal.getStatus());
        return vo;
    }
}
