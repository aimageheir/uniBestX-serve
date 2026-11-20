package com.unibestx.serve.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "刷新/退出登录请求参数")
@Data
public class RefreshTokenDTO {

    @Schema(description = "登录时返回的 refreshToken")
    private String refreshToken;
}
