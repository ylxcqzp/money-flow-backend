package com.ghost.moneyflowbackend.common.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 鉴权过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWT 服务
     */
    private final JwtService jwtService;

    /**
     * 用户详情服务
     */
    private final SysUserDetailsService userDetailsService;

    /**
     * 过滤请求并建立安全上下文
     *
     * @param request 请求对象
     * @param response 响应对象
     * @param filterChain 过滤器链
     * @throws ServletException 过滤异常
     * @throws IOException IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 从请求头解析 Token，未携带则直接放行
        String token = resolveToken(request);
        // 仅在当前上下文未认证时才进行解析，避免重复认证
        if (StringUtils.hasText(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 从 Token 中读取邮箱作为身份标识
                String email = jwtService.getSubject(token);
                if (StringUtils.hasText(email)) {
                    // 根据邮箱加载用户详情，确保用户状态有效
                    SysUserDetails userDetails = (SysUserDetails) userDetailsService.loadUserByUsername(email);
                    // 构建认证对象并写入安全上下文
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    // 绑定请求细节，便于后续审计与问题排查
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException ex) {
                // Token 无效时清理上下文，交由后续处理器返回未认证响应
                log.warn("JWT解析失败: {}", ex.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 解析请求头中的 Token
     *
     * @param request 请求对象
     * @return Token字符串
     */
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            return null;
        }
        // 约定格式：Bearer {token}
        return header.substring(7);
    }
}
