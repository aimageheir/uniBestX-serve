package com.unibestx.serve.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统用户实体，对应表 {@code sys_user}。
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;

    /** 手机号 */
    private String phone;

    /** 头像地址 */
    private String avatar;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记：0 未删除，1 已删除。
     * <p>
     * MyBatis-Plus 对 BaseMapper 的查询会自动追加 {@code deleted = 0}，
     * 删除会自动改写为 {@code UPDATE ... SET deleted = 1}。
     */
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
}
