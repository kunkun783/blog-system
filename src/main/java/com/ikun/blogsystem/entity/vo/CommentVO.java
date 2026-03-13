package com.ikun.blogsystem.entity.vo;

import com.ikun.blogsystem.entity.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentVO extends Comment {

    // 用户信息
    private String authorNickname;
    private String authorAvatar;

    // 回复目标信息 (如果 parentId 不为0)
    private String targetNickname;

    // 当前登录用户是否已点赞
    private Boolean isLiked;

    // 子评论
    private List<CommentVO> children;
}
