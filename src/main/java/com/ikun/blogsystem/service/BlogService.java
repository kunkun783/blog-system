package com.ikun.blogsystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.Blog;
import com.ikun.blogsystem.entity.dto.BlogPublishDTO;
import com.ikun.blogsystem.entity.vo.BlogVO;

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

    /**
     * 发布博文
     */
    Result<Void> publishBlog(BlogPublishDTO blogPublishDTO);

    /**
     * 删除博文
     */
    Result<Void> deleteBlog(Long blogId);

    /**
     * 获取博文详情
     */
    Result<BlogVO> getBlogDetail(Long blogId);

    /**
     * 点赞博文
     */
    Result<Void> likeBlog(Long blogId);

    /**
     * 取消点赞
     */
    Result<Void> unlikeBlog(Long blogId);

    /**
     * 收藏博文
     */
    Result<Void> collectBlog(Long blogId);

    /**
     * 取消收藏
     */
    Result<Void> uncollectBlog(Long blogId);

    /**
     * 根据用户ID分页获取博文
     */
    Result<Page<BlogVO>> getBlogsByUserId(Long userId, Integer current, Integer size);

    /**
     * 分页搜索博文（关键字、类别）
     */
    Result<Page<BlogVO>> listBlogs(String keyword, Integer categoryId, Integer current, Integer size);
}
