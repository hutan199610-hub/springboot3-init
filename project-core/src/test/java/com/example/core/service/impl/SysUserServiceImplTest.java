package com.example.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.exception.BusinessException;
import com.example.core.dto.SysUserDTO;
import com.example.core.entity.SysUser;
import com.example.core.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysUserServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private SysUserServiceImpl sysUserService;

    private SysUser testUser;

    @BeforeEach
    void setUp() {
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("admin");
        testUser.setPassword("password");
        testUser.setNickname("管理员");
        testUser.setStatus(1);
    }

    @Test
    void getById_fromCache() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sys:user:1")).thenReturn(testUser);

        SysUser result = sysUserService.getById(1L);

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        verify(sysUserMapper, never()).selectById(anyLong());
    }

    @Test
    void getById_fromDatabase() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sys:user:1")).thenReturn(null);
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);

        SysUser result = sysUserService.getById(1L);

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        verify(valueOperations).set(eq("sys:user:1"), eq(testUser), eq(30L), eq(TimeUnit.MINUTES));
    }

    @Test
    void getById_notFound() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("sys:user:1")).thenReturn(null);
        when(sysUserMapper.selectById(1L)).thenReturn(null);

        SysUser result = sysUserService.getById(1L);

        assertNull(result);
        verify(valueOperations, never()).set(anyString(), any(), anyLong(), any());
    }

    @Test
    void create_success() {
        when(sysUserMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(sysUserMapper.insert(any(SysUser.class))).thenReturn(1);

        SysUserDTO dto = new SysUserDTO();
        dto.setUsername("newuser");
        dto.setPassword("password123");

        assertDoesNotThrow(() -> sysUserService.create(dto));
        verify(sysUserMapper).insert(any(SysUser.class));
    }

    @Test
    void create_duplicateUsername() {
        when(sysUserMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        SysUserDTO dto = new SysUserDTO();
        dto.setUsername("admin");
        dto.setPassword("password123");

        BusinessException ex = assertThrows(BusinessException.class, () -> sysUserService.create(dto));
        assertEquals("用户名已存在", ex.getMessage());
    }

    @Test
    void update_success() {
        when(sysUserMapper.selectById(1L)).thenReturn(testUser);
        when(sysUserMapper.updateById(any(SysUser.class))).thenReturn(1);
        when(redisTemplate.delete("sys:user:1")).thenReturn(true);

        SysUserDTO dto = new SysUserDTO();
        dto.setId(1L);
        dto.setUsername("updated");

        assertDoesNotThrow(() -> sysUserService.update(dto));
        verify(redisTemplate).delete("sys:user:1");
    }

    @Test
    void update_notFound() {
        when(sysUserMapper.selectById(1L)).thenReturn(null);

        SysUserDTO dto = new SysUserDTO();
        dto.setId(1L);

        BusinessException ex = assertThrows(BusinessException.class, () -> sysUserService.update(dto));
        assertEquals("用户不存在", ex.getMessage());
    }

    @Test
    void delete_success() {
        when(sysUserMapper.deleteById(1L)).thenReturn(1);
        when(redisTemplate.delete("sys:user:1")).thenReturn(true);

        assertDoesNotThrow(() -> sysUserService.delete(1L));
        verify(sysUserMapper).deleteById(1L);
        verify(redisTemplate).delete("sys:user:1");
    }

    @Test
    void page_withUsername() {
        Page<SysUser> page = new Page<>(1, 10);
        page.setRecords(java.util.List.of(testUser));
        page.setTotal(1);
        when(sysUserMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<SysUser> result = sysUserService.page(1, 10, "admin");

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals("admin", result.getRecords().get(0).getUsername());
    }

    @Test
    void page_withoutUsername() {
        Page<SysUser> page = new Page<>(1, 10);
        page.setRecords(java.util.List.of(testUser));
        page.setTotal(1);
        when(sysUserMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<SysUser> result = sysUserService.page(1, 10, null);

        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
    }
}
