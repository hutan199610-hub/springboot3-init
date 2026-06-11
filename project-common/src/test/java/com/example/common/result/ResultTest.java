package com.example.common.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void successWithData() {
        Result<String> result = Result.success("hello");
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMsg());
        assertEquals("hello", result.getData());
    }

    @Test
    void successWithoutData() {
        Result<Void> result = Result.success();
        assertEquals(200, result.getCode());
        assertEquals("success", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    void errorWithMessage() {
        Result<Void> result = Result.error("failed");
        assertEquals(500, result.getCode());
        assertEquals("failed", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    void errorWithCodeAndMessage() {
        Result<Void> result = Result.error(400, "bad request");
        assertEquals(400, result.getCode());
        assertEquals("bad request", result.getMsg());
        assertNull(result.getData());
    }
}
