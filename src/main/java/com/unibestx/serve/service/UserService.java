package com.unibestx.serve.service;

import com.unibestx.serve.entity.User;

import java.util.List;

public interface UserService {

    User getById(Long id);

    List<User> list(String username);

    User create(User user);

    User update(User user);

    boolean delete(Long id);
}
