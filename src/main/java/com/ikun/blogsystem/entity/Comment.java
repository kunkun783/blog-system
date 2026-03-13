package com.ikun.blogsystem.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long blogId;

    private Long userId;

    /**
     * 父评论ID，0表示根评论
     */
    private Long parentId;

    private String content;

    private Integer likesCount;


    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime publishTime;
}
