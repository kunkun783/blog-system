package com.ikun.blogsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {
    /**
     * 审核评论 (管理员)
     * @param commentId 评论ID
     * @param status 审核状态：1-审核通过，2-审核驳回
     * @return Result<Void>
     */
    Result<Void> auditComment(Long commentId, Integer status);

    /**
     * 获取待审核评论列表
     */
    Result<List<Comment>> getPendingComments();
}
