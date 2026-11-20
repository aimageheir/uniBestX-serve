package com.unibestx.serve.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "创建用户请求参数")
@Data
public class UserCreateDTO {

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Schema(description = "密码", example = "123456")
    private String password;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "头像地址", example = "https://example.com/avatar.png")
    private String avatar;
}
