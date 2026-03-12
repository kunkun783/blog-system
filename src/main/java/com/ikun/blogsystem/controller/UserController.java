package com.ikun.blogsystem.controller;

import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.dto.UserLoginDTO;
import com.ikun.blogsystem.entity.dto.UserRegisterDTO;
import com.ikun.blogsystem.service.FileService;
import com.ikun.blogsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

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

    @PutMapping("/nickname")
    public Result<Void> updateNickname(@RequestBody Map<String, String> request) {
        String newNickname = request.get("nickname");
        return userService.updateNickname(newNickname);
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> request) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        return userService.updatePassword(oldPassword, newPassword);
    }

    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        // 1. 上传文件并获取 URL
        String avatarUrl = fileService.uploadAvatar(file);

        // 2. 更新用户的头像信息
        userService.updateAvatar(avatarUrl);

        // 3. 返回新的头像 URL
        Map<String, String> data = new HashMap<>();
        data.put("avatarUrl", avatarUrl);
        return Result.success(data);
    }

    @PostMapping("/follow/{userId}")
    public Result<Void> followUser(@PathVariable Long userId) {
        return userService.followUser(userId);
    }

    @DeleteMapping("/follow/{userId}")
    public Result<Void> unfollowUser(@PathVariable Long userId) {
        return userService.unfollowUser(userId);
    }
}
