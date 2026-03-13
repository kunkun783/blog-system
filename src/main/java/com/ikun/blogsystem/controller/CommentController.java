package com.ikun.blogsystem.controller;

import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.Comment;
import com.ikun.blogsystem.entity.vo.CommentVO;
import com.ikun.blogsystem.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 根据博文ID获取评论列表
     */
    @GetMapping("/list/{blogId}")
    public Result<List<CommentVO>> listByBlogId(@PathVariable Long blogId) {
        return commentService.getCommentsByBlogId(blogId);
    }

    /**
     * 发表评论/回复
     */
    @PostMapping
    public Result<Void> add(@RequestBody Comment comment) {
        return commentService.addComment(comment);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return commentService.deleteComment(id);
    }

    /**
     * 点赞评论
     */
    @PostMapping("/like/{id}")
    public Result<Void> like(@PathVariable Long id) {
        return commentService.likeComment(id);
    }
}
