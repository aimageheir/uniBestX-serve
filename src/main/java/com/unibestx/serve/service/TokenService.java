package com.unibestx.serve.service;

public interface TokenService {

    String issueRefreshToken(Long userId);

    Long consumeRefreshToken(String refreshToken);

    void revoke(String refreshToken);
}
