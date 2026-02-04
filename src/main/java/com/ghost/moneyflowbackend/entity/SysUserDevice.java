package com.ghost.moneyflowbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 用户设备信息实体
 */
@Data
@TableName("sys_user_device")
public class SysUserDevice {

    /**
     * 设备记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 客户端类型（web, android, ios, pc）
     */
    @NotBlank(message = "客户端类型不能为空")
    private String clientType;

    /**
     * 设备唯一标识
     */
    @NotBlank(message = "设备唯一标识不能为空")
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备推送Token
     */
    private String deviceToken;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 状态（0正常 1禁用）
     */
    private Integer status;

    /**
     * 首次绑定时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
