package com.example.admin.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.common.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthInterceptor.class);
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String USER_CACHE_KEY = "auth:token:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public JwtAuthInterceptor(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(TOKEN_PREFIX)) {
            return true;
        }

        String token = authHeader.substring(TOKEN_PREFIX.length());
        Object userInfo = redisTemplate.opsForValue().get(USER_CACHE_KEY + token);
        if (userInfo == null) {
            log.warn("Token 无效或已过期");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "Token 无效或已过期")));
            return false;
        }

        redisTemplate.expire(USER_CACHE_KEY + token, java.time.Duration.ofMinutes(30));
        request.setAttribute("currentUserInfo", userInfo);
        return true;
    }
}
