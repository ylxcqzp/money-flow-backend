package com.ghost.moneyflowbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ghost.moneyflowbackend.common.exception.BusinessException;
import com.ghost.moneyflowbackend.common.exception.ErrorCode;
import com.ghost.moneyflowbackend.common.security.JwtService;
import com.ghost.moneyflowbackend.common.security.SysUserDetails;
import com.ghost.moneyflowbackend.entity.SysUser;
import com.ghost.moneyflowbackend.mapper.SysUserMapper;
import com.ghost.moneyflowbackend.model.dto.AuthLoginRequest;
import com.ghost.moneyflowbackend.model.dto.AuthRegisterRequest;
import com.ghost.moneyflowbackend.model.vo.AuthLoginResponse;
import com.ghost.moneyflowbackend.model.vo.AuthMeResponse;
import com.ghost.moneyflowbackend.model.vo.AuthUserVO;
import com.ghost.moneyflowbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 登录注册鉴权服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    /**
     * 用户数据访问层
     */
    private final SysUserMapper sysUserMapper;

    /**
     * 密码加密器
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * 认证管理器
     */
    private final AuthenticationManager authenticationManager;

    /**
     * JWT 服务
     */
    private final JwtService jwtService;

    /**
     * 用户登录
     *
     * @param request 登录参数
     * @return 登录结果
     */
    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {
        String email = request.getEmail();
        log.info("开始处理登录请求，邮箱: {}", email);
        try {
            // 构造认证请求对象，交由 Spring Security 校验账号密码
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, request.getPassword());
            authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException ex) {
            // 统一转为业务异常，避免暴露认证细节
            log.warn("登录失败，邮箱: {}, 原因: {}", email, ex.getMessage());
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "邮箱或密码错误");
        }
        // 认证通过后再次加载用户，确保账号状态有效
        SysUser user = getValidUserByEmail(email);
        // 签发 JWT 供前端后续请求使用
        String token = jwtService.generateToken(user);
        log.info("登录成功，用户ID: {}", user.getId());
        return buildLoginResponse(user, token);
    }

    /**
     * 用户注册
     *
     * @param request 注册参数
     * @return 注册结果
     */
    @Override
    public AuthLoginResponse register(AuthRegisterRequest request) {
        String email = request.getEmail();
        log.info("开始处理注册请求，邮箱: {}", email);
        // 邮箱唯一性校验，避免重复注册
        if (existsByEmail(email)) {
            log.warn("注册失败，邮箱已存在: {}", email);
            throw new BusinessException(ErrorCode.CONFLICT, "邮箱已被注册");
        }
        // 组装用户实体并进行安全初始化
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setNickname(request.getUsername());
        user.setEmail(email);
        // 明文密码必须加密后落库
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus(0);
        user.setDelFlag(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        int inserted = sysUserMapper.insert(user);
        // 插入失败时直接抛出异常，避免产生脏数据
        if (inserted != 1 || user.getId() == null) {
            log.error("注册失败，用户插入异常，邮箱: {}", email);
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "注册失败，请稍后重试");
        }
        // 注册成功后直接签发 Token，实现注册即登录
        String token = jwtService.generateToken(user);
        log.info("注册成功，用户ID: {}", user.getId());
        return buildLoginResponse(user, token);
    }

    /**
     * 获取当前用户信息
     *
     * @param userDetails 认证用户详情
     * @return 当前用户信息
     */
    @Override
    public AuthMeResponse currentUser(SysUserDetails userDetails) {
        if (userDetails == null) {
            log.warn("获取当前用户信息失败，认证信息为空");
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或登录已失效");
        }
        // 从认证信息中获取用户实体并转换为视图对象
        AuthMeResponse response = new AuthMeResponse();
        response.setUser(buildUserVO(userDetails.getUser()));
        return response;
    }

    /**
     * 根据邮箱查询有效用户
     *
     * @param email 邮箱
     * @return 用户实体
     */
    private SysUser getValidUserByEmail(String email) {
        // 仅查询未删除用户，避免已删除账号登录
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, email)
                .eq(SysUser::getDelFlag, 0);
        SysUser user = sysUserMapper.selectOne(wrapper);
        if (user == null) {
            log.warn("用户不存在，邮箱: {}", email);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "邮箱或密码错误");
        }
        // 账号被停用时拒绝登录
        if (user.getStatus() != null && user.getStatus() == 1) {
            log.warn("账号已停用，用户ID: {}", user.getId());
            throw new BusinessException(ErrorCode.FORBIDDEN, "账号已停用");
        }
        return user;
    }

    /**
     * 校验邮箱是否已存在
     *
     * @param email 邮箱
     * @return 是否存在
     */
    private boolean existsByEmail(String email) {
        // 查询未删除用户，避免被逻辑删除账号占用邮箱
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, email)
                .eq(SysUser::getDelFlag, 0);
        return sysUserMapper.selectCount(wrapper) > 0;
    }

    /**
     * 构建登录响应对象
     *
     * @param user 用户实体
     * @param token JWT Token
     * @return 登录响应
     */
    private AuthLoginResponse buildLoginResponse(SysUser user, String token) {
        // 组装登录返回结构，包含 Token 与用户信息
        AuthLoginResponse response = new AuthLoginResponse();
        response.setToken(token);
        response.setUser(buildUserVO(user));
        return response;
    }

    /**
     * 构建用户信息视图对象
     *
     * @param user 用户实体
     * @return 用户信息视图
     */
    private AuthUserVO buildUserVO(SysUser user) {
        // 仅暴露必要字段，避免敏感信息泄露
        AuthUserVO userVO = new AuthUserVO();
        userVO.setId(user.getId());
        userVO.setUsername(user.getUsername());
        userVO.setNickname(user.getNickname());
        userVO.setEmail(user.getEmail());
        userVO.setAvatarUrl(user.getAvatarUrl());
        return userVO;
    }
}
