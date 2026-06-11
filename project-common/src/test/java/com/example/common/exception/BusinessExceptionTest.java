package com.example.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessExceptionTest {

    @Test
    void defaultMessage() {
        BusinessException ex = new BusinessException("error");
        assertEquals(500, ex.getCode());
        assertEquals("error", ex.getMessage());
    }

    @Test
    void customCode() {
        BusinessException ex = new BusinessException(400, "bad request");
        assertEquals(400, ex.getCode());
        assertEquals("bad request", ex.getMessage());
    }
}
