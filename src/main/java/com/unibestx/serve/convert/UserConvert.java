package com.unibestx.serve.convert;

import com.unibestx.serve.dto.UserCreateDTO;
import com.unibestx.serve.dto.UserUpdateDTO;
import com.unibestx.serve.entity.SysUser;
import com.unibestx.serve.vo.UserVO;

import java.util.List;

public final class UserConvert {

    private UserConvert() {
    }

    public static UserVO toVO(SysUser user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateTime(user.getUpdateTime());
        return vo;
    }

    public static List<UserVO> toVOList(List<SysUser> users) {
        return users.stream().map(UserConvert::toVO).toList();
    }

    public static SysUser toEntity(UserCreateDTO dto) {
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        return user;
    }

    public static void apply(UserUpdateDTO dto, SysUser user) {
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
    }
}
