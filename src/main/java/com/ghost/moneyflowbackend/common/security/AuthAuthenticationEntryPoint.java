package com.ghost.moneyflowbackend.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghost.moneyflowbackend.common.exception.ErrorCode;
import com.ghost.moneyflowbackend.common.model.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 未认证访问处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * JSON 序列化工具
     */
    private final ObjectMapper objectMapper;

    /**
     * 处理未认证请求
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param authException 认证异常
     * @throws IOException IO异常
     * @throws ServletException 处理异常
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.warn("未认证访问: {}", authException.getMessage());
        Result<Void> result = Result.fail(ErrorCode.UNAUTHORIZED, "未登录或登录已失效");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
