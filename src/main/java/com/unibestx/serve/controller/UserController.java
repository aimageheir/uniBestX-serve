package com.unibestx.serve.controller;

import com.unibestx.serve.convert.UserConvert;
import com.unibestx.serve.dto.UserCreateDTO;
import com.unibestx.serve.dto.UserUpdateDTO;
import com.unibestx.serve.entity.SysUser;
import com.unibestx.serve.service.UserService;
import com.unibestx.serve.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "用户管理", description = "用户查询、创建、更新、删除")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "获取当前用户信息（示例接口）")
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    public ResponseEntity<UserVO> getUserInfo() {
        UserVO userVO =  new UserVO();
        userVO.setId(123L);
        userVO.setUsername("username");
        userVO.setCreateTime(LocalDateTime.now());
        userVO.setUpdateTime(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.OK).body(userVO);
    }


    @Operation(summary = "查询用户列表")
    @GetMapping("/users")
    public List<UserVO> list(
            @Parameter(description = "用户名，模糊匹配") @RequestParam(required = false) String username) {
        return UserConvert.toVOList(userService.list(username));
    }

    @Operation(summary = "根据 ID 查询用户")
    @GetMapping("/users/{id}")
    public UserVO get(@Parameter(description = "用户 ID") @PathVariable Long id) {
        return UserConvert.toVO(userService.getById(id));
    }

    @Operation(summary = "创建用户")
    @PostMapping("/users")
    public UserVO create(@RequestBody UserCreateDTO dto) {
        SysUser user = userService.create(UserConvert.toEntity(dto));
        return UserConvert.toVO(user);
    }

    @Operation(summary = "更新用户")
    @PutMapping("/users/{id}")
    public UserVO update(@Parameter(description = "用户 ID") @PathVariable Long id,
                         @RequestBody UserUpdateDTO dto) {
        SysUser user = new SysUser();
        user.setId(id);
        UserConvert.apply(dto, user);
        return UserConvert.toVO(userService.update(user));
    }

    @Operation(summary = "删除用户（逻辑删除）")
    @DeleteMapping("/users/{id}")
    public Boolean delete(@Parameter(description = "用户 ID") @PathVariable Long id) {
        return userService.delete(id);
    }
}
