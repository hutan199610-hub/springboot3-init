package com.example.core.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SysUserDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void create_valid() {
        SysUserDTO dto = new SysUserDTO();
        dto.setUsername("admin");
        dto.setPassword("123456");

        Set<ConstraintViolation<SysUserDTO>> violations = validator.validate(dto, SysUserDTO.CreateGroup.class);
        assertTrue(violations.isEmpty());
    }

    @Test
    void create_missingUsername() {
        SysUserDTO dto = new SysUserDTO();
        dto.setPassword("123456");

        Set<ConstraintViolation<SysUserDTO>> violations = validator.validate(dto, SysUserDTO.CreateGroup.class);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void create_missingPassword() {
        SysUserDTO dto = new SysUserDTO();
        dto.setUsername("admin");

        Set<ConstraintViolation<SysUserDTO>> violations = validator.validate(dto, SysUserDTO.CreateGroup.class);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void update_passwordOptional() {
        SysUserDTO dto = new SysUserDTO();
        dto.setId(1L);
        dto.setUsername("admin");

        Set<ConstraintViolation<SysUserDTO>> violations = validator.validate(dto, SysUserDTO.UpdateGroup.class);
        assertTrue(violations.isEmpty());
    }

    @Test
    void update_missingId() {
        SysUserDTO dto = new SysUserDTO();
        dto.setUsername("admin");

        Set<ConstraintViolation<SysUserDTO>> violations = validator.validate(dto, SysUserDTO.UpdateGroup.class);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("id")));
    }

    @Test
    void usernameTooShort() {
        SysUserDTO dto = new SysUserDTO();
        dto.setUsername("a");
        dto.setPassword("123456");

        Set<ConstraintViolation<SysUserDTO>> violations = validator.validate(dto, SysUserDTO.CreateGroup.class);
        assertFalse(violations.isEmpty());
    }

    @Test
    void passwordTooShort() {
        SysUserDTO dto = new SysUserDTO();
        dto.setUsername("admin");
        dto.setPassword("123");

        Set<ConstraintViolation<SysUserDTO>> violations = validator.validate(dto, SysUserDTO.CreateGroup.class);
        assertFalse(violations.isEmpty());
    }
}
