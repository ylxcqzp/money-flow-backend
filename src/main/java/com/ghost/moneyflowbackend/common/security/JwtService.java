package com.ghost.moneyflowbackend.common.security;

import com.ghost.moneyflowbackend.common.exception.BusinessException;
import com.ghost.moneyflowbackend.common.exception.ErrorCode;
import com.ghost.moneyflowbackend.entity.SysUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

/**
 * JWT 处理服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {

    /**
     * 用户ID载荷字段
     */
    public static final String CLAIM_USER_ID = "userId";

    /**
     * 认证配置
     */
    private final AuthProperties authProperties;

    /**
     * 生成 JWT Token
     *
     * @param user 用户实体
     * @return Token字符串
     */
    public String generateToken(SysUser user) {
        // 当前时间作为签发时间
        Instant now = Instant.now();
        // 过期时间由配置决定，避免硬编码
        Instant expireAt = now.plusSeconds(authProperties.getExpireMinutes() * 60L);
        return Jwts.builder()
                // 签发者用于区分 Token 归属系统
                .issuer(authProperties.getIssuer())
                // subject 使用邮箱，便于与认证逻辑保持一致
                .subject(user.getEmail())
                // 在载荷中写入用户ID，减少后续查询成本
                .claim(CLAIM_USER_ID, user.getId())
                // 设置签发与过期时间，便于自动校验有效期
                .issuedAt(Date.from(now))
                .expiration(Date.from(expireAt))
                // 使用配置密钥进行签名
                .signWith(getSignKey())
                .compact();
    }

    /**
     * 解析 Token 获取 Claims
     *
     * @param token Token字符串
     * @return Claims内容
     */
    public Claims parseToken(String token) {
        // 解析时自动验证签名与过期时间
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取 Token 中的邮箱
     *
     * @param token Token字符串
     * @return 邮箱
     */
    public String getSubject(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 获取 Token 中的用户ID
     *
     * @param token Token字符串
     * @return 用户ID
     */
    public Long getUserId(String token) {
        // 从载荷读取用户ID并做类型兼容
        Claims claims = parseToken(token);
        Object value = claims.get(CLAIM_USER_ID);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            log.warn("Token用户ID解析失败: {}", value, ex);
            return null;
        }
    }

    /**
     * 获取签名密钥
     *
     * @return 签名密钥
     */
    private SecretKey getSignKey() {
        // 使用 UTF-8 编码确保跨平台一致性
        byte[] secretBytes = authProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "JWT密钥长度不足");
        }
        // 基于 HMAC-SHA 算法生成密钥
        return Keys.hmacShaKeyFor(secretBytes);
    }
}
