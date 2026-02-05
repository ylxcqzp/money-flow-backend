package com.ghost.moneyflowbackend.common.utils;

import com.ghost.moneyflowbackend.common.security.SysUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 安全工具类，用于获取当前登录用户信息
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户ID
     *
     * @return 用户ID
     * @throws IllegalStateException 如果当前用户未认证
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("当前用户未认证");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof SysUserDetails) {
            return ((SysUserDetails) principal).getUser().getId();
        }
        throw new IllegalStateException("无法获取用户信息");
    }

    /**
     * 获取当前登录用户详情
     *
     * @return 用户详情
     * @throws IllegalStateException 如果当前用户未认证
     */
    public static SysUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("当前用户未认证");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof SysUserDetails) {
            return (SysUserDetails) principal;
        }
        throw new IllegalStateException("无法获取用户详情");
    }

    /**
     * 获取当前登录用户实体
     *
     * @return 用户实体
     * @throws IllegalStateException 如果当前用户未认证
     */
    public static com.ghost.moneyflowbackend.entity.SysUser getCurrentUser() {
        return getCurrentUserDetails().getUser();
    }

    /**
     * 检查当前用户是否已认证
     *
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * 获取当前用户名
     *
     * @return 用户名
     * @throws IllegalStateException 如果当前用户未认证
     */
    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    /**
     * 获取当前用户昵称
     *
     * @return 用户昵称
     * @throws IllegalStateException 如果当前用户未认证
     */
    public static String getCurrentNickname() {
        return getCurrentUser().getNickname();
    }

    /**
     * 获取当前用户邮箱
     *
     * @return 用户邮箱
     * @throws IllegalStateException 如果当前用户未认证
     */
    public static String getCurrentEmail() {
        return getCurrentUser().getEmail();
    }
}