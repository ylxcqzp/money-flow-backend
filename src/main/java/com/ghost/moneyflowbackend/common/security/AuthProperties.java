package com.ghost.moneyflowbackend.common.security;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 认证配置参数
 */
@Data
@Validated
@ConfigurationProperties(prefix = "security.jwt")
public class AuthProperties {

    /**
     * JWT 签名密钥
     */
    @NotBlank(message = "JWT密钥不能为空")
    private String secret;

    /**
     * JWT 过期时间（分钟）
     */
    @Min(value = 1, message = "JWT过期时间必须大于0")
    private Integer expireMinutes = 120;

    /**
     * JWT 签发者标识
     */
    @NotBlank(message = "JWT签发者不能为空")
    private String issuer = "money-flow-backend";
}
