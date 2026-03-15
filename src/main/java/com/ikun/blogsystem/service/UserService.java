package com.ikun.blogsystem.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.User;
import com.ikun.blogsystem.entity.dto.UserLoginDTO;
import com.ikun.blogsystem.entity.dto.UserRegisterDTO;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {
    /**
     * 用户注册，直接返回 Result
     */
    Result<Void> register(UserRegisterDTO registerDTO);

    /**
     * 用户登录，返回包含 Token 的 Result
     */
    Result<String> login(UserLoginDTO loginDTO);

    /**
     * 修改昵称
     * @param newNickname 新昵称
     * @return Result<Void>
     */
    Result<Void> updateNickname(String newNickname);

    /**
     * 修改密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return Result<Void>
     */
    Result<Void> updatePassword(String oldPassword, String newPassword);

    /**
     * 更新用户头像
     * @param avatarUrl 新头像的 URL
     * @return Result<Void>
     */
    Result<Void> updateAvatar(String avatarUrl);

    /**
     * 关注用户
     * @param followUserId 要关注的用户ID
     * @return Result<Void>
     */
    Result<Void> followUser(Long followUserId);

    /**
     * 取消关注用户
     * @param followUserId 要取消关注的用户ID
     * @return Result<Void>
     */
    Result<Void> unfollowUser(Long followUserId);

    Result<List<User>> getFollowList();

    Result<Map<String, String>> getUserInfo();
}
