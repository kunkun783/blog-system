package com.ikun.blogsystem.entity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class BlogPublishDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotNull(message = "分类不能为空")
    private Integer categoryId;

    private List<String> imageUrls;
}
