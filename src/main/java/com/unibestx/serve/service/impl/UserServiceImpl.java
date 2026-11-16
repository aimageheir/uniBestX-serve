package com.unibestx.serve.service.impl;

import com.unibestx.serve.entity.User;
import com.unibestx.serve.exception.BusinessException;
import com.unibestx.serve.mapper.UserMapper;
import com.unibestx.serve.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.selectUserById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    @Override
    public List<User> list(String username) {
        return userMapper.selectUserList(username);
    }

    @Override
    public User create(User user) {
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new BusinessException(400, "用户名已存在");
        }
        userMapper.insertUser(user);
        return userMapper.selectUserById(user.getId());
    }

    @Override
    public User update(User user) {
        User existing = userMapper.selectUserById(user.getId());
        if (existing == null) {
            throw new BusinessException(404, "用户不存在");
        }
        userMapper.updateUser(user);
        return userMapper.selectUserById(user.getId());
    }

    @Override
    public boolean delete(Long id) {
        if (userMapper.selectUserById(id) == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return userMapper.deleteUserById(id) > 0;
    }
}
