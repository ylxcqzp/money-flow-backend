package com.ghost.moneyflowbackend.common.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ghost.moneyflowbackend.entity.SysUser;
import com.ghost.moneyflowbackend.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户详情服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserDetailsService implements UserDetailsService {

    /**
     * 用户数据访问层
     */
    private final SysUserMapper sysUserMapper;

    /**
     * 根据邮箱加载用户信息
     *
     * @param username 邮箱地址
     * @return 用户详情
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getEmail, username)
                .eq(SysUser::getDelFlag, 0);
        SysUser user = sysUserMapper.selectOne(wrapper);
        if (user == null) {
            log.warn("未找到用户: {}", username);
            throw new UsernameNotFoundException("用户不存在");
        }
        return new SysUserDetails(user);
    }
}
