package com.example.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.Result;
import com.example.core.dto.SysUserDTO;
import com.example.core.entity.SysUser;
import com.example.core.service.SysUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    private static final int MAX_PAGE_SIZE = 100;

    private final SysUserService sysUserService;

    public SysUserController(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        return Result.success(sysUserService.getById(id));
    }

    @GetMapping("/page")
    public Result<Page<SysUser>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username) {
        pageSize = Math.min(pageSize, MAX_PAGE_SIZE);
        return Result.success(sysUserService.page(pageNum, pageSize, username));
    }

    @PostMapping
    public Result<Void> create(@Validated(SysUserDTO.CreateGroup.class) @RequestBody SysUserDTO dto) {
        sysUserService.create(dto);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@Validated(SysUserDTO.UpdateGroup.class) @RequestBody SysUserDTO dto) {
        sysUserService.update(dto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return Result.success();
    }
}
