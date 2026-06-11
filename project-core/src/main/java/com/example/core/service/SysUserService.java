package com.example.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.core.dto.SysUserDTO;
import com.example.core.entity.SysUser;

public interface SysUserService {

    SysUser getById(Long id);

    Page<SysUser> page(int pageNum, int pageSize, String username);

    void create(SysUserDTO dto);

    void update(SysUserDTO dto);

    void delete(Long id);
}
