package com.unibestx.serve.service.impl;

import com.unibestx.serve.entity.User;
import com.unibestx.serve.exception.BusinessException;
import com.unibestx.serve.mapper.UserMapper;
import com.unibestx.serve.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getById(Long id) {
        User user = findById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    @Override
    public User findById(Long id) {
        return userMapper.selectUserById(id);
    }

    @Override
    public List<User> list(String username) {
        return userMapper.selectUserList(username);
    }

    @Override
    public User create(User user) {
        return insertWithEncodedPassword(user);
    }

    @Override
    public User register(User user) {
        if (isBlank(user.getUsername()) || isBlank(user.getPassword())) {
            throw new BusinessException(400, "用户名和密码不能为空");
        }
        return insertWithEncodedPassword(user);
    }

    @Override
    public User login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        User user = userMapper.selectByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        return user;
    }

    @Override
    public User update(User user) {
        User existing = userMapper.selectUserById(user.getId());
        if (existing == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (isBlank(user.getUsername())) {
            user.setUsername(existing.getUsername());
        } else if (!user.getUsername().equals(existing.getUsername())
                && userMapper.selectByUsername(user.getUsername()) != null) {
            throw new BusinessException(400, "用户名已存在");
        }
        if (isBlank(user.getPassword())) {
            user.setPassword(existing.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
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

    private User insertWithEncodedPassword(User user) {
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new BusinessException(400, "用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insertUser(user);
        return userMapper.selectUserById(user.getId());
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
