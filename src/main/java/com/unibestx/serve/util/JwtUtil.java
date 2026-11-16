package com.unibestx.serve.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

import java.nio.charset.StandardCharsets;

public final class JwtUtil {

    private static final String DEFAULT_SECRET = "unibestx-serve-default-secret";
    private static final long EXPIRE_HOURS = 24;

    private JwtUtil() {
    }

    public static String generate(String username) {
        return generate(username, DEFAULT_SECRET);
    }

    public static String generate(String username, String secret) {
        return JWT.create()
                .setPayload("sub", username)
                .setPayload("exp", DateUtil.offsetHour(DateUtil.date(), (int) EXPIRE_HOURS))
                .setKey(secret.getBytes(StandardCharsets.UTF_8))
                .sign();
    }

    public static boolean verify(String token, String secret) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return JWT.of(token).setKey(secret.getBytes(StandardCharsets.UTF_8)).verify();
    }

    public static String getUsername(String token) {
        return String.valueOf(JWTUtil.parseToken(token).getPayload("sub"));
    }
}
