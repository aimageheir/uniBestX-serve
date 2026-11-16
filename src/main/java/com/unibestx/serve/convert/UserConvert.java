package com.unibestx.serve.convert;

import com.unibestx.serve.dto.UserCreateDTO;
import com.unibestx.serve.dto.UserUpdateDTO;
import com.unibestx.serve.entity.User;
import com.unibestx.serve.vo.UserVO;

import java.util.List;

public final class UserConvert {

    private UserConvert() {
    }

    public static UserVO toVO(User user) {
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

    public static List<UserVO> toVOList(List<User> users) {
        return users.stream().map(UserConvert::toVO).toList();
    }

    public static User toEntity(UserCreateDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        return user;
    }

    public static void apply(UserUpdateDTO dto, User user) {
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
    }
}
