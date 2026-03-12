package com.ikun.blogsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.Comment;
import com.ikun.blogsystem.mapper.CommentMapper;
import com.ikun.blogsystem.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public Result<Void> auditComment(Long commentId, Integer status) {
        Comment comment = this.getById(commentId);
        if (comment == null) {
            return Result.error(404, "该评论不存在");
        }
        
        // 状态验证：1-通过，2-驳回
        if (status != 1 && status != 2) {
            return Result.error(400, "审核状态错误");
        }
        
        comment.setAuditStatus(status);
        this.updateById(comment);
        return Result.success();
    }

    @Override
    public Result<List<Comment>> getPendingComments() {
        List<Comment> comments = this.list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getAuditStatus, 0));
        return Result.success(comments);
    }
}
