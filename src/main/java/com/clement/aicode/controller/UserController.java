package com.clement.aicode.controller;

import com.clement.aicode.common.Result;
import com.clement.aicode.model.dto.UserLoginRequest;
import com.clement.aicode.model.dto.UserRegisterRequest;
import com.clement.aicode.model.vo.UserLoginVO;
import com.clement.aicode.model.vo.UserVO;
import com.clement.aicode.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody UserRegisterRequest request) {
        return Result.success(userService.register(request));
    }

    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginRequest request) {
        return Result.success(userService.login(request));
    }

    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }
}
