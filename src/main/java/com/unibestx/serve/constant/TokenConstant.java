package com.unibestx.serve.constant;

import java.time.Duration;

public final class TokenConstant {

    public static final String REFRESH_TOKEN_PREFIX = "auth:refresh:";
    public static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);

    private TokenConstant() {
    }
}
