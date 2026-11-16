package com.unibestx.serve.controller;

import com.unibestx.serve.convert.UserConvert;
import com.unibestx.serve.dto.UserCreateDTO;
import com.unibestx.serve.dto.UserUpdateDTO;
import com.unibestx.serve.entity.User;
import com.unibestx.serve.result.Result;
import com.unibestx.serve.service.UserService;
import com.unibestx.serve.vo.UserVO;
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

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    public Result<String> getUserInfo() {
        return Result.ok("getUserInfo");
    }

    @GetMapping("/users")
    public Result<List<UserVO>> list(@RequestParam(required = false) String username) {
        return Result.ok(UserConvert.toVOList(userService.list(username)));
    }

    @GetMapping("/users/{id}")
    public Result<UserVO> get(@PathVariable Long id) {
        return Result.ok(UserConvert.toVO(userService.getById(id)));
    }

    @PostMapping("/users")
    public Result<UserVO> create(@RequestBody UserCreateDTO dto) {
        User user = userService.create(UserConvert.toEntity(dto));
        return Result.ok(UserConvert.toVO(user));
    }

    @PutMapping("/users/{id}")
    public Result<UserVO> update(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        User user = new User();
        user.setId(id);
        UserConvert.apply(dto, user);
        return Result.ok(UserConvert.toVO(userService.update(user)));
    }

    @DeleteMapping("/users/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.ok(userService.delete(id));
    }
}
