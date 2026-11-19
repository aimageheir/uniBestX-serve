package com.unibestx.serve.vo;

import lombok.Data;

@Data
public class LoginVO {

    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private long refreshExpiresIn;
    private String username;
}
