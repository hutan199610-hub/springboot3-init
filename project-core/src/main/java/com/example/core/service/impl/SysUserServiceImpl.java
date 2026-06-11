package com.example.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.core.dto.SysUserDTO;
import com.example.core.entity.SysUser;
import com.example.core.mapper.SysUserMapper;
import com.example.core.service.SysUserService;
import com.example.common.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_CACHE_KEY = "sys:user:";

    public SysUserServiceImpl(SysUserMapper sysUserMapper, RedisTemplate<String, Object> redisTemplate) {
        this.sysUserMapper = sysUserMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public SysUser getById(Long id) {
        String cacheKey = USER_CACHE_KEY + id;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof SysUser user) {
            return user;
        }
        SysUser user = sysUserMapper.selectById(id);
        if (user != null) {
            redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);
        }
        return user;
    }

    @Override
    public Page<SysUser> page(int pageNum, int pageSize, String username) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(SysUser::getUsername, username);
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        return sysUserMapper.selectPage(page, wrapper);
    }

    @Override
    public void create(SysUserDTO dto) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        if (sysUserMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setStatus(1);
        sysUserMapper.insert(user);
    }

    @Override
    public void update(SysUserDTO dto) {
        SysUser user = sysUserMapper.selectById(dto.getId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        BeanUtils.copyProperties(dto, user, "password");
        sysUserMapper.updateById(user);
        redisTemplate.delete(USER_CACHE_KEY + dto.getId());
    }

    @Override
    public void delete(Long id) {
        sysUserMapper.deleteById(id);
        redisTemplate.delete(USER_CACHE_KEY + id);
    }
}
