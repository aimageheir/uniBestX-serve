package com.unibestx.serve.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;
    private String username;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
