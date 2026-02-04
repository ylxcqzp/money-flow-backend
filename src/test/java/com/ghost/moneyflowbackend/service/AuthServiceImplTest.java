package com.ghost.moneyflowbackend.service;

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
import com.ghost.moneyflowbackend.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 登录注册鉴权服务测试
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    /**
     * 用户数据访问层
     */
    @Mock
    private SysUserMapper sysUserMapper;

    /**
     * 密码加密器
     */
    @Mock
    private PasswordEncoder passwordEncoder;

    /**
     * 认证管理器
     */
    @Mock
    private AuthenticationManager authenticationManager;

    /**
     * JWT 服务
     */
    @Mock
    private JwtService jwtService;

    /**
     * 服务实例
     */
    @InjectMocks
    private AuthServiceImpl authService;

    /**
     * 测试注册成功流程
     */
    @Test
    void registerShouldReturnTokenAndUser() {
        AuthRegisterRequest request = new AuthRegisterRequest();
        request.setUsername("测试用户");
        request.setEmail("user@example.com");
        request.setPassword("password123");

        Mockito.when(sysUserMapper.selectCount(Mockito.any())).thenReturn(0L);
        Mockito.when(passwordEncoder.encode("password123")).thenReturn("hashed");
        Mockito.when(jwtService.generateToken(Mockito.any())).thenReturn("jwt-token");
        Mockito.when(sysUserMapper.insert(Mockito.any(SysUser.class))).thenAnswer(invocation -> {
            SysUser user = invocation.getArgument(0);
            user.setId(1L);
            return 1;
        });

        AuthLoginResponse response = authService.register(request);

        Assertions.assertEquals("jwt-token", response.getToken());
        Assertions.assertEquals(1L, response.getUser().getId());
        Assertions.assertEquals("user@example.com", response.getUser().getEmail());
        Assertions.assertEquals("测试用户", response.getUser().getUsername());
    }

    /**
     * 测试注册时邮箱重复
     */
    @Test
    void registerShouldThrowWhenEmailExists() {
        AuthRegisterRequest request = new AuthRegisterRequest();
        request.setUsername("测试用户");
        request.setEmail("user@example.com");
        request.setPassword("password123");

        Mockito.when(sysUserMapper.selectCount(Mockito.any())).thenReturn(1L);

        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> authService.register(request));
        Assertions.assertEquals(ErrorCode.CONFLICT, exception.getCode());
    }

    /**
     * 测试登录成功流程
     */
    @Test
    void loginShouldReturnTokenWhenValid() {
        AuthLoginRequest request = new AuthLoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        SysUser user = new SysUser();
        user.setId(9L);
        user.setEmail("user@example.com");
        user.setUsername("测试用户");
        user.setStatus(0);
        user.setDelFlag(0);
        Mockito.when(sysUserMapper.selectOne(Mockito.any())).thenReturn(user);
        Mockito.when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthLoginResponse response = authService.login(request);

        Assertions.assertEquals("jwt-token", response.getToken());
        Assertions.assertEquals(9L, response.getUser().getId());
        Assertions.assertEquals("user@example.com", response.getUser().getEmail());
    }

    /**
     * 测试登录失败时抛出异常
     */
    @Test
    void loginShouldThrowWhenBadCredentials() {
        AuthLoginRequest request = new AuthLoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("bad");

        Mockito.when(authenticationManager.authenticate(Mockito.any()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> authService.login(request));
        Assertions.assertEquals(ErrorCode.UNAUTHORIZED, exception.getCode());
    }

    /**
     * 测试获取当前用户信息成功
     */
    @Test
    void currentUserShouldReturnUserInfo() {
        SysUser user = new SysUser();
        user.setId(7L);
        user.setUsername("测试用户");
        user.setEmail("user@example.com");
        user.setStatus(0);
        SysUserDetails details = new SysUserDetails(user);

        AuthMeResponse response = authService.currentUser(details);

        Assertions.assertEquals(7L, response.getUser().getId());
        Assertions.assertEquals("user@example.com", response.getUser().getEmail());
    }

    /**
     * 测试获取当前用户信息时认证为空
     */
    @Test
    void currentUserShouldThrowWhenNull() {
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> authService.currentUser(null));
        Assertions.assertEquals(ErrorCode.UNAUTHORIZED, exception.getCode());
    }
}
