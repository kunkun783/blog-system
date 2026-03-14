package com.ikun.blogsystem.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.common.result.ResultCode;
import com.ikun.blogsystem.entity.User;
import com.ikun.blogsystem.entity.UserFollow;
import com.ikun.blogsystem.mapper.UserFollowMapper;
import com.ikun.blogsystem.mapper.UserMapper;
import com.ikun.blogsystem.entity.dto.UserLoginDTO;
import com.ikun.blogsystem.entity.dto.UserRegisterDTO;
import com.ikun.blogsystem.service.UserService;
import com.ikun.blogsystem.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserFollowMapper userFollowMapper;

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

    @Override
    public Result<Void> updateNickname(String newNickname) {
        // 1. 从 SecurityContext 获取当前用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. 检查新昵称是否已被占用
        User existNickname = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getNickname, newNickname)
                .ne(User::getUsername, username)); // 排除当前用户自身
        if (existNickname != null) {
            return Result.error(500, "该昵称已被占用");
        }

        // 3. 更新昵称
        User currentUser = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        currentUser.setNickname(newNickname);
        this.updateById(currentUser);

        return Result.success();
    }

    @Override
    public Result<Void> updatePassword(String oldPassword, String newPassword) {
        // 1. 从 SecurityContext 获取当前用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. 查找用户
        User currentUser = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));

        // 3. 校验旧密码
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }

        // 4. 更新密码
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(currentUser);

        return Result.success();
    }

    @Override
    public Result<Void> updateAvatar(String avatarUrl) {
        // 1. 从 SecurityContext 获取当前用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. 查找用户并更新头像
        User currentUser = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        currentUser.setAvatarUrl(avatarUrl);
        this.updateById(currentUser);

        return Result.success();
    }

    @Override
    public Result<Void> followUser(Long followUserId) {
        // 1. 获取当前用户ID
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Long userId = currentUser.getId();

        // 2. 不能关注自己
        if (userId.equals(followUserId)) {
            return Result.error(500, "不能关注自己");
        }

        // 3. 检查是否已关注
        UserFollow existFollow = userFollowMapper.selectOne(new LambdaQueryWrapper<UserFollow>()
                .eq(UserFollow::getFollowerId, userId)
                .eq(UserFollow::getFollowingId, followUserId));

        if (existFollow != null) {
            return Result.error(500, "您已关注该用户");
        }

        // 4. 创建关注关系
        UserFollow userFollow = new UserFollow();
        userFollow.setFollowerId(userId);
        userFollow.setFollowingId(followUserId);
        userFollowMapper.insert(userFollow);

        return Result.success();
    }

    @Override
    public Result<Void> unfollowUser(Long followUserId) {
        // 1. 获取当前用户ID
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Long userId = currentUser.getId();

        // 2. 删除关注关系
        userFollowMapper.delete(new LambdaQueryWrapper<UserFollow>()
                .eq(UserFollow::getFollowerId, userId)
                .eq(UserFollow::getFollowingId, followUserId));

        return Result.success();
    }

    @Override
    public Result<List<User>> getFollowList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = this.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Long userId = currentUser.getId();

        List<Long> followUserIds = userFollowMapper.selectList(new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getFollowerId, userId))
                .stream()
                .map(UserFollow::getFollowingId)
                .collect(Collectors.toList());

        if (followUserIds.isEmpty()) {
            return Result.success(Collections.emptyList());
        }

        List<User> users = this.listByIds(followUserIds);
        users.forEach(user -> user.setPassword(null));
        return Result.success(users);
    }
}
