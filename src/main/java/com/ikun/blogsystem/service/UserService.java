package com.ikun.blogsystem.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.User;
import com.ikun.blogsystem.entity.dto.UserLoginDTO;
import com.ikun.blogsystem.entity.dto.UserRegisterDTO;

public interface UserService extends IService<User> {
    /**
     * 用户注册，直接返回 Result
     */
    Result<Void> register(UserRegisterDTO registerDTO);

    /**
     * 用户登录，返回包含 Token 的 Result
     */
    Result<String> login(UserLoginDTO loginDTO);
}
