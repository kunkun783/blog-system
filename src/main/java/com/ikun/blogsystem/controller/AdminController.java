package com.ikun.blogsystem.controller;

import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.Blog;
import com.ikun.blogsystem.entity.Comment;
import com.ikun.blogsystem.service.BlogService;
import com.ikun.blogsystem.service.CategoryService;
import com.ikun.blogsystem.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    /**
     * 添加博文类别
     */
    @PostMapping("/category")
    public Result<Void> addCategory(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        return categoryService.addCategory(name);
    }

    /**
     * 获取待审核博文列表
     */
    @GetMapping("/blog/pending")
    public Result<List<Blog>> getPendingBlogs() {
        return blogService.getPendingBlogs();
    }

    /**
     * 博文审批
     * @param request 包含 blogId 和 status (1-通过, 2-驳回)
     */
    @PutMapping("/blog/audit")
    public Result<Void> auditBlog(@RequestBody Map<String, Object> request) {
        Long blogId = Long.valueOf(request.get("blogId").toString());
        Integer status = Integer.valueOf(request.get("status").toString());
        return blogService.auditBlog(blogId, status);
    }

    /**
     * 获取待审核评论列表
     */
    @GetMapping("/comment/pending")
    public Result<List<Comment>> getPendingComments() {
        return commentService.getPendingComments();
    }

    /**
     * 评论审批
     * @param request 包含 commentId 和 status (1-通过, 2-驳回)
     */
    @PutMapping("/comment/audit")
    public Result<Void> auditComment(@RequestBody Map<String, Object> request) {
        Long commentId = Long.valueOf(request.get("commentId").toString());
        Integer status = Integer.valueOf(request.get("status").toString());
        return commentService.auditComment(commentId, status);
    }
}
