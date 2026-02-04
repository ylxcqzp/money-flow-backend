package com.ghost.moneyflowbackend.common.security;

import com.ghost.moneyflowbackend.entity.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Spring Security 用户详情实现
 */
public class SysUserDetails implements UserDetails {

    /**
     * 系统用户实体
     */
    private final SysUser user;

    /**
     * 构建用户详情
     *
     * @param user 用户实体
     */
    public SysUserDetails(SysUser user) {
        this.user = user;
    }

    /**
     * 获取系统用户ID
     *
     * @return 用户ID
     */
    public Long getUserId() {
        return user.getId();
    }

    /**
     * 获取用户邮箱
     *
     * @return 邮箱
     */
    public String getEmail() {
        return user.getEmail();
    }

    /**
     * 获取用户实体
     *
     * @return 用户实体
     */
    public SysUser getUser() {
        return user;
    }

    /**
     * 获取用户权限集合
     *
     * @return 权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    /**
     * 获取密码哈希
     *
     * @return 密码哈希
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * 获取用户名（此处使用邮箱作为登录账号）
     *
     * @return 登录账号
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * 判断账号是否未过期
     *
     * @return 是否未过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 判断账号是否未锁定
     *
     * @return 是否未锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 判断凭证是否未过期
     *
     * @return 是否未过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 判断账号是否启用
     *
     * @return 是否启用
     */
    @Override
    public boolean isEnabled() {
        return user.getStatus() == null || user.getStatus() == 0;
    }
}
