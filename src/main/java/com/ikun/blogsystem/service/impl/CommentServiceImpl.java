package com.ikun.blogsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.Blog;
import com.ikun.blogsystem.entity.Comment;
import com.ikun.blogsystem.entity.CommentLike;
import com.ikun.blogsystem.entity.User;
import com.ikun.blogsystem.entity.vo.CommentVO;
import com.ikun.blogsystem.mapper.BlogMapper;
import com.ikun.blogsystem.mapper.CommentLikeMapper;
import com.ikun.blogsystem.mapper.CommentMapper;
import com.ikun.blogsystem.mapper.UserMapper;
import com.ikun.blogsystem.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    @Autowired
    private BlogMapper blogMapper;

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            return user != null ? user.getId() : null;
        } else if (principal instanceof String && !"anonymousUser".equals(principal)) {
            String username = (String) principal;
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            return user != null ? user.getId() : null;
        }
        return null;
    }

    @Override
    public Result<List<CommentVO>> getCommentsByBlogId(Long blogId) {
        List<Comment> allComments = this.list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getBlogId, blogId)
                .orderByAsc(Comment::getPublishTime));

        List<CommentVO> voList = allComments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建层级结构
        Map<Long, List<CommentVO>> parentMap = voList.stream()
                .filter(vo -> vo.getParentId() != 0)
                .collect(Collectors.groupingBy(CommentVO::getParentId));

        voList.forEach(vo -> vo.setChildren(parentMap.get(vo.getId())));

        return Result.success(voList.stream()
                .filter(vo -> vo.getParentId() == 0)
                .collect(Collectors.toList()));
    }

    @Override
    public Result<Void> addComment(Comment comment) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        comment.setUserId(userId);
        comment.setLikesCount(0);
        comment.setPublishTime(LocalDateTime.now());
        this.save(comment);
        // 对应博文的评论数+1
        blogMapper.update(null, new LambdaUpdateWrapper<Blog>()
                .setSql("comments_count = comments_count + 1")
                .eq(Blog::getId, comment.getBlogId()));
        return Result.success();
    }

    @Override
    public Result<Void> deleteComment(Long commentId) {
        Comment comment = this.getById(commentId);
        if (comment == null) {
            return Result.error(404, "评论不存在");
        }
        Long userId = getCurrentUserId();
        if (!comment.getUserId().equals(userId)) {
            return Result.error(403, "无权删除他人评论");
        }
        this.removeById(commentId);
        // 对应博文的评论数-1
        blogMapper.update(null, new LambdaUpdateWrapper<Blog>()
                .setSql("comments_count = GREATEST(0, comments_count - 1)")
                .eq(Blog::getId, comment.getBlogId()));
        return Result.success();
    }

    @Override
    public Result<Void> likeComment(Long commentId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        CommentLike existLike = commentLikeMapper.selectOne(new LambdaQueryWrapper<CommentLike>()
                .eq(CommentLike::getUserId, userId)
                .eq(CommentLike::getCommentId, commentId));

        if (existLike != null) {
            // 取消点赞
            commentLikeMapper.deleteById(existLike.getId());
            this.update().setSql("likes_count = GREATEST(0, likes_count - 1)").eq("id", commentId).update();
        } else {
            // 点赞
            CommentLike commentLike = new CommentLike();
            commentLike.setUserId(userId);
            commentLike.setCommentId(commentId);
            commentLikeMapper.insert(commentLike);
            this.update().setSql("likes_count = likes_count + 1").eq("id", commentId).update();
        }
        return Result.success();
    }

    private CommentVO convertToVO(Comment comment) {
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);

        // 作者信息
        User author = userMapper.selectById(comment.getUserId());
        if (author != null) {
            vo.setAuthorNickname(author.getNickname());
            vo.setAuthorAvatar(author.getAvatarUrl());
        }

        // 回复目标信息
        if (comment.getParentId() != 0) {
            Comment parentComment = this.getById(comment.getParentId());
            if (parentComment != null) {
                User targetUser = userMapper.selectById(parentComment.getUserId());
                if (targetUser != null) {
                    vo.setTargetNickname(targetUser.getNickname());
                }
            }
        }

        // 当前用户是否点赞
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            vo.setIsLiked(commentLikeMapper.selectCount(new LambdaQueryWrapper<CommentLike>()
                    .eq(CommentLike::getUserId, currentUserId)
                    .eq(CommentLike::getCommentId, comment.getId())) > 0);
        } else {
            vo.setIsLiked(false);
        }

        return vo;
    }
}
