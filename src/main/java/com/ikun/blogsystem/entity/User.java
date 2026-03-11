package com.ikun.blogsystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String nickname;

    private String password;

    private Integer fansCount;

    private Integer followCount;

    /**
     * 用户角色：1-普通用户，2-管理员
     */
    private Integer userRole;

    private String avatarUrl;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
