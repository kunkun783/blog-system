package com.ikun.blogsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.Blog;
import com.ikun.blogsystem.mapper.BlogMapper;
import com.ikun.blogsystem.service.BlogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Override
    public Result<Void> auditBlog(Long blogId, Integer status) {
        Blog blog = this.getById(blogId);
        if (blog == null) {
            return Result.error(404, "该博文不存在");
        }
        
        // 状态验证：1-通过，2-驳回
        if (status != 1 && status != 2) {
            return Result.error(400, "审核状态错误");
        }
        
        blog.setAuditStatus(status);
        this.updateById(blog);
        return Result.success();
    }

    @Override
    public Result<List<Blog>> getPendingBlogs() {
        List<Blog> blogs = this.list(new LambdaQueryWrapper<Blog>()
                .eq(Blog::getAuditStatus, 0));
        return Result.success(blogs);
    }
}
