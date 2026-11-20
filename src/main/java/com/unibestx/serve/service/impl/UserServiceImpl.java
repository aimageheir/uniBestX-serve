package com.unibestx.serve.service.impl;

import com.unibestx.serve.entity.SysUser;
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
    public SysUser getById(Long id) {
        SysUser user = findById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    @Override
    public SysUser findById(Long id) {
        return userMapper.selectUserById(id);
    }

    @Override
    public List<SysUser> list(String username) {
        return userMapper.selectUserList(username);
    }

    @Override
    public SysUser create(SysUser user) {
        return insertWithEncodedPassword(user);
    }

    @Override
    public SysUser register(SysUser user) {
        if (isBlank(user.getUsername()) || isBlank(user.getPassword())) {
            throw new BusinessException(400, "用户名和密码不能为空");
        }
        return insertWithEncodedPassword(user);
    }

    @Override
    public SysUser login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            throw new BusinessException(401, "用户名和密码不能为空");
        }
        SysUser user = userMapper.selectByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        return user;
    }

    @Override
    public SysUser update(SysUser user) {
        SysUser existing = userMapper.selectUserById(user.getId());
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
        // 手机号、头像为可选字段：不传（null）保持原值，传空串表示清空
        if (user.getPhone() == null) {
            user.setPhone(existing.getPhone());
        }
        if (user.getAvatar() == null) {
            user.setAvatar(existing.getAvatar());
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

    private SysUser insertWithEncodedPassword(SysUser user) {
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
