package com.unibestx.serve.service;

import com.unibestx.serve.entity.SysUser;

import java.util.List;

public interface UserService {

    SysUser getById(Long id);

    SysUser findById(Long id);

    List<SysUser> list(String username);

    SysUser create(SysUser user);

    SysUser register(SysUser user);

    SysUser login(String username, String password);

    SysUser update(SysUser user);

    boolean delete(Long id);
}
