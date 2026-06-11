package com.example.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SysUserDTO {

    @NotNull(message = "用户ID不能为空", groups = UpdateGroup.class)
    private Long id;

    @NotBlank(message = "用户名不能为空", groups = {CreateGroup.class, UpdateGroup.class})
    @Size(min = 2, max = 50, message = "用户名长度需在2-50之间", groups = {CreateGroup.class, UpdateGroup.class})
    private String username;

    @NotBlank(message = "密码不能为空", groups = CreateGroup.class)
    @Size(min = 6, max = 100, message = "密码长度需在6-100之间", groups = CreateGroup.class)
    private String password;

    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    @Size(max = 20, message = "手机号长度不能超过20")
    private String phone;

    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;

    private Integer status;

    public interface CreateGroup {}
    public interface UpdateGroup {}
}
