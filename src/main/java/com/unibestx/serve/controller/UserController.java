package com.unibestx.serve.controller;

import com.unibestx.serve.convert.UserConvert;
import com.unibestx.serve.dto.UserCreateDTO;
import com.unibestx.serve.dto.UserUpdateDTO;
import com.unibestx.serve.entity.User;
import com.unibestx.serve.service.UserService;
import com.unibestx.serve.vo.UserVO;
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

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    public ResponseEntity<UserVO> getUserInfo() {
        UserVO userVO =  new UserVO();
        userVO.setId(123L);
        userVO.setUsername("username");
        userVO.setCreateTime(LocalDateTime.now());
        userVO.setUpdateTime(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.OK).body(userVO);
    }


    @GetMapping("/users")
    public List<UserVO> list(@RequestParam(required = false) String username) {
        return UserConvert.toVOList(userService.list(username));
    }

    @GetMapping("/users/{id}")
    public UserVO get(@PathVariable Long id) {
        return UserConvert.toVO(userService.getById(id));
    }

    @PostMapping("/users")
    public UserVO create(@RequestBody UserCreateDTO dto) {
        User user = userService.create(UserConvert.toEntity(dto));
        return UserConvert.toVO(user);
    }

    @PutMapping("/users/{id}")
    public UserVO update(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        User user = new User();
        user.setId(id);
        UserConvert.apply(dto, user);
        return UserConvert.toVO(userService.update(user));
    }

    @DeleteMapping("/users/{id}")
    public Boolean delete(@PathVariable Long id) {
        return userService.delete(id);
    }
}
