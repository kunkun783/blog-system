package com.ikun.blogsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.Category;
import com.ikun.blogsystem.mapper.CategoryMapper;
import com.ikun.blogsystem.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public Result<List<Category>> getAllCategories() {
        return Result.success(this.list());
    }

    @Override
    public Result<Void> addCategory(String name) {
        // 检查分类名是否已存在
        Category existCategory = this.getOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, name));
        if (existCategory != null) {
            return Result.error(500, "该分类已存在");
        }

        Category category = new Category();
        category.setName(name);
        this.save(category);
        return Result.success();
    }

    @Override
    public Result<Void> deleteCategory(Integer id) {
        Category category = this.getById(id);
        if (category == null) {
            return Result.error(404, "该分类不存在");
        }
        this.removeById(id);
        return Result.success();
    }
}
