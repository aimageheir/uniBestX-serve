package com.unibestx.serve.controller;

import com.unibestx.serve.constant.TokenConstant;
import com.unibestx.serve.convert.UserConvert;
import com.unibestx.serve.dto.LoginDTO;
import com.unibestx.serve.dto.RefreshTokenDTO;
import com.unibestx.serve.dto.UserCreateDTO;
import com.unibestx.serve.entity.User;
import com.unibestx.serve.exception.BusinessException;
import com.unibestx.serve.result.ResultCode;
import com.unibestx.serve.service.TokenService;
import com.unibestx.serve.service.UserService;
import com.unibestx.serve.util.JwtUtil;
import com.unibestx.serve.vo.LoginVO;
import com.unibestx.serve.vo.UserVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;

    public AuthController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public UserVO register(@RequestBody UserCreateDTO dto) {
        User user = userService.register(UserConvert.toEntity(dto));
        return UserConvert.toVO(user);
    }

    @PostMapping("/login")
    public LoginVO login(@RequestBody LoginDTO dto) {
        User user = userService.login(dto.getUsername(), dto.getPassword());
        return issueTokens(user);
    }

    @PostMapping("/refresh")
    public LoginVO refresh(@RequestBody RefreshTokenDTO dto) {
        Long userId = tokenService.consumeRefreshToken(dto.getRefreshToken());
        User user = userId == null ? null : userService.findById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED.getCode(), "登录已过期，请重新登录");
        }
        return issueTokens(user);
    }

    @PostMapping("/logout")
    public void logout(@RequestBody RefreshTokenDTO dto) {
        tokenService.revoke(dto.getRefreshToken());
    }

    private LoginVO issueTokens(User user) {
        LoginVO vo = new LoginVO();
        vo.setToken(JwtUtil.generate(user.getId()));
        vo.setRefreshToken(tokenService.issueRefreshToken(user.getId()));
        vo.setExpiresIn(JwtUtil.EXPIRE_SECONDS);
        vo.setRefreshExpiresIn(TokenConstant.REFRESH_TOKEN_TTL.toSeconds());
        vo.setUsername(user.getUsername());
        return vo;
    }
}
