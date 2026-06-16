package com.example.auth.controller;
import com.example.auth.model.dto.ApiResponse;
import com.example.auth.model.dto.LoginRequest;
import com.example.auth.model.dto.RegisterRequest;
import com.example.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * 认证控制器 —— 对外暴露 RESTful API
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册接口  POST /api/auth/register
     */
    @PostMapping("/register")
    public ApiResponse<String> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ApiResponse.success("注册成功");
    }

    /**
     * 用户登录接口  POST /api/auth/login
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> data = userService.login(request);
        return ApiResponse.success("登录成功", data);
    }

    /**
     * 获取当前登录用户信息  GET /api/auth/me
     * （需携带有效 Token 才能访问）
     */
    @GetMapping("/me")
    public ApiResponse<String> getCurrentUser(Principal principal) {
        return ApiResponse.success("当前用户：" + principal.getName());
    }
}