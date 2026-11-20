package com.unibestx.serve.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "登录结果")
@Data
public class LoginVO {

    @Schema(description = "访问令牌，请求时放到 accessToken 请求头")
    private String token;

    @Schema(description = "刷新令牌，用于换取新的访问令牌")
    private String refreshToken;

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "访问令牌有效期（秒）", example = "7200")
    private long expiresIn;

    @Schema(description = "刷新令牌有效期（秒）", example = "604800")
    private long refreshExpiresIn;

    @Schema(description = "用户名", example = "zhangsan")
    private String username;
}
