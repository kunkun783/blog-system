package com.ikun.blogsystem.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.common.result.ResultCode;
import com.ikun.blogsystem.entity.User;
import com.ikun.blogsystem.mapper.UserMapper;
import com.ikun.blogsystem.entity.dto.UserLoginDTO;
import com.ikun.blogsystem.entity.dto.UserRegisterDTO;
import com.ikun.blogsystem.service.UserService;
import com.ikun.blogsystem.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Result<Void> register(UserRegisterDTO registerDTO) {
        // 1. 检查用户名是否已存在
        User existUsername = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, registerDTO.getUsername()));
        if (existUsername != null) {
            return Result.error(500, "该用户名已被占用");
        }

        // 2. 检查昵称是否已存在
        User existNickname = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getNickname, registerDTO.getNickname()));
        if (existNickname != null) {
            return Result.error(500, "该昵称已被占用");
        }

        // 3. 创建并保存
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setNickname(registerDTO.getNickname());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setUserRole(1);
        user.setCreateTime(LocalDateTime.now());

        this.save(user);
        return Result.success();
    }

    @Override
    public Result<String> login(UserLoginDTO loginDTO) {
        // 1. 根据用户名查找
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginDTO.getUsername()));

        if (user == null) {
            return Result.error(ResultCode.USER_NOT_FOUND);
        }

        // 2. 校验密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        // 3. 生成 Token (Payload 建议携带 username 或 nickname)
        String token = jwtUtils.createToken(user.getId(), user.getUsername(), user.getUserRole());
        return Result.success(token);
    }
}