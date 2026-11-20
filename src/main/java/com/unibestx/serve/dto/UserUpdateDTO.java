package com.unibestx.serve.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "更新用户请求参数")
@Data
public class UserUpdateDTO {

    @Schema(description = "用户名，不传则保持原值", example = "zhangsan")
    private String username;

    @Schema(description = "密码，不传则保持原值", example = "123456")
    private String password;
}
