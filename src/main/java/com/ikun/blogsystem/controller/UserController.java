package com.ikun.blogsystem.controller;

import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.dto.UserLoginDTO;
import com.ikun.blogsystem.entity.dto.UserRegisterDTO;
import com.ikun.blogsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserRegisterDTO registerDTO) {
        // 直接返回 Service 层包装好的 Result
        return userService.register(registerDTO);
    }

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody UserLoginDTO loginDTO) {
        Result<String> loginResult = userService.login(loginDTO);

        // 如果登录失败，将错误结果透传
        if (loginResult.getCode() != 200) {
            return Result.error(loginResult.getCode(), loginResult.getMsg());
        }

        // 组装前端需要的 Map 格式
        Map<String, String> data = new HashMap<>();
        data.put("token", loginResult.getData());
        return Result.success(data);
    }
}
