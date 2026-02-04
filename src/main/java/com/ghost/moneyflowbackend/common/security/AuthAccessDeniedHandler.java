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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 无权限访问处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * JSON 序列化工具
     */
    private final ObjectMapper objectMapper;

    /**
     * 处理无权限访问
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param accessDeniedException 权限异常
     * @throws IOException IO异常
     * @throws ServletException 处理异常
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("无权限访问: {}", accessDeniedException.getMessage());
        Result<Void> result = Result.fail(ErrorCode.FORBIDDEN, "无权限访问");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
