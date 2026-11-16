package com.unibestx.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unibestx.serve.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    User selectUserById(@Param("id") Long id);

    User selectByUsername(@Param("username") String username);

    List<User> selectUserList(@Param("username") String username);

    int insertUser(User user);

    int updateUser(User user);

    int deleteUserById(@Param("id") Long id);
}
