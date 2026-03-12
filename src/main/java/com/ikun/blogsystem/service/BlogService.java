package com.ikun.blogsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.Blog;

import java.util.List;

public interface BlogService extends IService<Blog> {
    /**
     * 审核博文 (管理员)
     * @param blogId 博文ID
     * @param status 审核状态：1-审核通过，2-审核驳回
     * @return Result<Void>
     */
    Result<Void> auditBlog(Long blogId, Integer status);

    /**
     * 获取待审核博文列表
     */
    Result<List<Blog>> getPendingBlogs();
}
