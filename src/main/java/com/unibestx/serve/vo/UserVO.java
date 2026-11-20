package com.unibestx.serve.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户信息")
@Data
public class UserVO {

    @Schema(description = "用户 ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "头像地址", example = "https://example.com/avatar.png")
    private String avatar;

    @Schema(description = "创建时间", example = "2026-11-20 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2026-11-20 10:00:00")
    private LocalDateTime updateTime;
}
