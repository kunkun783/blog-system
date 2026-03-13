package com.ikun.blogsystem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("blog")
public class Blog {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer categoryId;

    private String title;

    private String content;

    /**
     * 博文图片，多张以逗号分隔，前端展示最多3张
     */
    private String imageUrls;

    private Integer likesCount;

    private Integer commentsCount;

    /**
     * 审核状态：0-待审核，1-审核通过，2-审核驳回
     */
    private Integer auditStatus;

    @TableField(fill = FieldFill.INSERT)
    @JsonIgnore
    private LocalDateTime publishTime;

}