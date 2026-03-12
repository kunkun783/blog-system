package com.ikun.blogsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ikun.blogsystem.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
