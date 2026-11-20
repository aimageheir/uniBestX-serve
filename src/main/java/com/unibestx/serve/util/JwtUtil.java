package com.unibestx.serve.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

import java.nio.charset.StandardCharsets;

public final class JwtUtil {

    private static final String DEFAULT_SECRET = "unibestx-serve-default-secret";
    private static final long EXPIRE_HOURS = 2;
    public static final long EXPIRE_SECONDS = EXPIRE_HOURS * 60 * 60;

    private JwtUtil() {
    }

    public static String generate(Long userId) {
        return generate(userId, DEFAULT_SECRET);
    }

    public static String generate(Long userId, String secret) {
        return JWT.create()
                .setPayload("sub", String.valueOf(userId))
                .setPayload("exp", DateUtil.offsetHour(DateUtil.date(), (int) EXPIRE_HOURS))
                .setKey(secret.getBytes(StandardCharsets.UTF_8))
                .sign();
    }

    /**
     * 校验 token 的签名。
     * <p>
     * 畸形 token（格式不合法）在 Hutool 解析阶段就会抛异常，这里统一按校验失败处理，
     * 避免异常从过滤器抛出后转发到 /error。
     */
    public static boolean verify(String token, String secret) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            return JWT.of(token).setKey(secret.getBytes(StandardCharsets.UTF_8)).verify();
        } catch (Exception e) {
            return false;
        }
    }

    public static Long getUserId(String token) {
        return Long.valueOf(String.valueOf(JWTUtil.parseToken(token).getPayload("sub")));
    }
}
