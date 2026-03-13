package com.ikun.blogsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.Comment;
import com.ikun.blogsystem.entity.vo.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {

    /**
     * 根据博文ID获取评论列表（层级结构）
     */
    Result<List<CommentVO>> getCommentsByBlogId(Long blogId);

    /**
     * 发表评论
     */
    Result<Void> addComment(Comment comment);

    /**
     * 删除评论
     */
    Result<Void> deleteComment(Long commentId);

    /**
     * 点赞/取消点赞评论
     */
    Result<Void> likeComment(Long commentId);
}
