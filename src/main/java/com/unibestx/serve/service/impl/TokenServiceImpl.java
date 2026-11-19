package com.unibestx.serve.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.unibestx.serve.constant.TokenConstant;
import com.unibestx.serve.service.TokenService;
import com.unibestx.serve.util.DigestUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    private final StringRedisTemplate redisTemplate;

    public TokenServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String issueRefreshToken(Long userId) {
        String refreshToken = RandomUtil.randomString(64);
        redisTemplate.opsForValue()
                .set(key(refreshToken), String.valueOf(userId), TokenConstant.REFRESH_TOKEN_TTL);
        return refreshToken;
    }

    @Override
    public Long consumeRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return null;
        }
        String userId = redisTemplate.opsForValue().getAndDelete(key(refreshToken));
        return userId == null ? null : Long.valueOf(userId);
    }

    @Override
    public void revoke(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        redisTemplate.delete(key(refreshToken));
    }

    private static String key(String refreshToken) {
        return TokenConstant.REFRESH_TOKEN_PREFIX + DigestUtil.sha256(refreshToken);
    }
}
