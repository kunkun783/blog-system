package com.ikun.blogsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_collection")
class UserCollection {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long blogId;
    private LocalDateTime createTime;
}
