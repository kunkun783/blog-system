package com.ikun.blogsystem.entity.vo;

import com.ikun.blogsystem.entity.Blog;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BlogVO extends Blog {
    private String authorNickname;
    private String authorAvatar;
    private String categoryName;
    private Boolean isLiked;
    private Boolean isCollected;
}
