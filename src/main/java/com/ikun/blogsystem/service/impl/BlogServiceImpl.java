package com.ikun.blogsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.Blog;
import com.ikun.blogsystem.entity.User;
import com.ikun.blogsystem.entity.UserCollection;
import com.ikun.blogsystem.entity.UserLike;
import com.ikun.blogsystem.entity.dto.BlogPublishDTO;
import com.ikun.blogsystem.entity.vo.BlogVO;
import com.ikun.blogsystem.mapper.*;
import com.ikun.blogsystem.service.BlogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserLikeMapper userLikeMapper;

    @Autowired
    private UserCollectionMapper userCollectionMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            return user != null ? user.getId() : null;
        } else if (principal instanceof String && !"anonymousUser".equals(principal)) {
            //兜底逻辑，以防某些情况下principal是String
            String username = (String) principal;
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
            return user != null ? user.getId() : null;
        }
        return null;
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_2"));
    }

    private BlogVO convertToVO(Blog blog) {
        BlogVO vo = new BlogVO();
        BeanUtils.copyProperties(blog, vo);
        
        // 作者信息
        User author = userMapper.selectById(blog.getUserId());
        if (author != null) {
            vo.setAuthorNickname(author.getNickname());
            vo.setAuthorAvatar(author.getAvatarUrl());
        }
        
        // 分类信息
        if (blog.getCategoryId() != null) {
            var category = categoryMapper.selectById(blog.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        
        // 当前用户状态
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            vo.setIsLiked(userLikeMapper.selectCount(new LambdaQueryWrapper<UserLike>()
                    .eq(UserLike::getUserId, currentUserId)
                    .eq(UserLike::getBlogId, blog.getId())) > 0);
            vo.setIsCollected(userCollectionMapper.selectCount(new LambdaQueryWrapper<UserCollection>()
                    .eq(UserCollection::getUserId, currentUserId)
                    .eq(UserCollection::getBlogId, blog.getId())) > 0);
        } else {
            vo.setIsLiked(false);
            vo.setIsCollected(false);
        }
        
        return vo;
    }

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

    @Override
    public Result<Void> publishBlog(BlogPublishDTO blogPublishDTO) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        
        Blog blog = new Blog();
        BeanUtils.copyProperties(blogPublishDTO, blog);

        // 处理图片
        if (!CollectionUtils.isEmpty(blogPublishDTO.getImageUrls())) {
            List<String> urls = blogPublishDTO.getImageUrls();
            if (urls.size() > 3) {
                urls = urls.subList(0, 3);
            }
            blog.setImageUrls(String.join(",", urls));
        }

        blog.setUserId(userId);
        blog.setAuditStatus(0); // 默认待审核
        blog.setLikesCount(0);
        blog.setCommentsCount(0);
        blog.setPublishTime(LocalDateTime.now());
        this.save(blog);
        return Result.success();
    }

    @Override
    public Result<Void> deleteBlog(Long blogId) {
        Blog blog = this.getById(blogId);
        if (blog == null) {
            return Result.error(404, "该博文不存在");
        }
        Long userId = getCurrentUserId();
        // 作者或管理员可删除
        if (!blog.getUserId().equals(userId) && !isAdmin()) {
            return Result.error(403, "无权删除他人博文");
        }
        this.removeById(blogId);
        return Result.success();
    }

    @Override
    public Result<BlogVO> getBlogDetail(Long blogId) {
        Blog blog = this.getById(blogId);
        if (blog == null) {
            return Result.error(404, "该博文不存在");
        }
        if (blog.getAuditStatus() != 1) {
            return Result.error(403, "博文审核中或未通过");
        }
        return Result.success(convertToVO(blog));
    }

    @Override
    public Result<Void> likeBlog(Long blogId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        
        UserLike existLike = userLikeMapper.selectOne(new LambdaQueryWrapper<UserLike>()
                .eq(UserLike::getUserId, userId)
                .eq(UserLike::getBlogId, blogId));
        
        if (existLike == null) {
            UserLike userLike = new UserLike();
            userLike.setUserId(userId);
            userLike.setBlogId(blogId);
            userLikeMapper.insert(userLike);
            this.update().setSql("likes_count = likes_count + 1").eq("id", blogId).update();
        }
        return Result.success();
    }

    @Override
    public Result<Void> unlikeBlog(Long blogId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        
        int deletedRows = userLikeMapper.delete(new LambdaQueryWrapper<UserLike>()
                .eq(UserLike::getUserId, userId)
                .eq(UserLike::getBlogId, blogId));
        
        if (deletedRows > 0) {
            this.update().setSql("likes_count = GREATEST(0, likes_count - 1)").eq("id", blogId).update();
        }
        return Result.success();
    }

    @Override
    public Result<Void> collectBlog(Long blogId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        UserCollection existCollect = userCollectionMapper.selectOne(new LambdaQueryWrapper<UserCollection>()
                .eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getBlogId, blogId));

        if (existCollect == null) {
            UserCollection userCollection = new UserCollection();
            userCollection.setUserId(userId);
            userCollection.setBlogId(blogId);
            userCollectionMapper.insert(userCollection);
        }
        return Result.success();
    }

    @Override
    public Result<Void> uncollectBlog(Long blogId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        userCollectionMapper.delete(new LambdaQueryWrapper<UserCollection>()
                .eq(UserCollection::getUserId, userId)
                .eq(UserCollection::getBlogId, blogId));
        return Result.success();
    }

    @Override
    public Result<Page<BlogVO>> getBlogsByUserId(Long userId, Integer current, Integer size) {
        Page<Blog> page = new Page<>(current, size);
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<Blog>()
                .eq(Blog::getUserId, userId)
                .eq(Blog::getAuditStatus, 1) // 只显示审核通过的
                .orderByDesc(Blog::getPublishTime);
        
        Page<Blog> blogPage = this.page(page, wrapper);
        Page<BlogVO> voPage = new Page<>(current, size, blogPage.getTotal());
        voPage.setRecords(blogPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        return Result.success(voPage);
    }

    @Override
    public Result<Page<BlogVO>> listBlogs(String keyword, Integer categoryId, Integer current, Integer size) {
        Page<Blog> page = new Page<>(current, size);
        LambdaQueryWrapper<Blog> wrapper = new LambdaQueryWrapper<Blog>()
                .eq(Blog::getAuditStatus, 1); // 只搜索审核通过的
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Blog::getTitle, keyword).or().like(Blog::getContent, keyword));
        }
        
        if (categoryId != null) {
            wrapper.eq(Blog::getCategoryId, categoryId);
        }
        
        wrapper.orderByDesc(Blog::getPublishTime);
        
        Page<Blog> blogPage = this.page(page, wrapper);
        Page<BlogVO> voPage = new Page<>(current, size, blogPage.getTotal());
        voPage.setRecords(blogPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        return Result.success(voPage);
    }
}
