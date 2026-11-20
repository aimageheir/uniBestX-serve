package com.unibestx.serve.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.unibestx.serve.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<SysUser> {

    SysUser selectUserById(@Param("id") Long id);

    SysUser selectByUsername(@Param("username") String username);

    List<SysUser> selectUserList(@Param("username") String username);

    int insertUser(SysUser user);

    int updateUser(SysUser user);

    int deleteUserById(@Param("id") Long id);
}
