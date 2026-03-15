package com.ikun.blogsystem.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.dto.BlogPublishDTO;
import com.ikun.blogsystem.entity.vo.BlogVO;
import com.ikun.blogsystem.service.BlogService;
import com.ikun.blogsystem.service.FileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private FileService fileService;

    /**
     * 发布博文
     */
    @PostMapping("/publish")
    public Result<Void> publish(@Valid @RequestBody BlogPublishDTO blogPublishDTO) {
        return blogService.publishBlog(blogPublishDTO);
    }

    /**
     * 删除博文
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        return blogService.deleteBlog(id);
    }

    /**
     * 查看博文详情
     */
    @GetMapping("/detail/{id}")
    public Result<BlogVO> detail(@PathVariable Long id) {
        return blogService.getBlogDetail(id);
    }

    /**
     * 点赞博文
     */
    @PostMapping("/like/{id}")
    public Result<Void> like(@PathVariable Long id) {
        return blogService.likeBlog(id);
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/like/{id}")
    public Result<Void> unlike(@PathVariable Long id) {
        return blogService.unlikeBlog(id);
    }

    /**
     * 收藏博文
     */
    @PostMapping("/collect/{id}")
    public Result<Void> collect(@PathVariable Long id) {
        return blogService.collectBlog(id);
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/collect/{id}")
    public Result<Void> uncollect(@PathVariable Long id) {
        return blogService.uncollectBlog(id);
    }

    /**
     * 首页博文列表（分页、分类、关键字）
     */
    @GetMapping("/list")
    public Result<Page<BlogVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return blogService.listBlogs(keyword, categoryId, current, size);
    }

    /**
     * 根据用户id分页查找博文
     */
    @GetMapping("/user/{userId}")
    public Result<Page<BlogVO>> listByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return blogService.getBlogsByUserId(userId, current, size);
    }

    @PostMapping("/image")
    public Result<Map<String, String>> uploadBlogImage(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadBlogImage(file);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return Result.success(data);
    }

    @GetMapping("/collect/list")
    public Result<Page<BlogVO>> collectList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return blogService.getCollectedBlogs(current, size);
    }
}
