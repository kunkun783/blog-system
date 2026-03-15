package com.ikun.blogsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    /**
     * 获取所有分类
     */
    Result<List<Category>> getAllCategories();

    /**
     * 添加分类 (管理员)
     */
    Result<Void> addCategory(String name);

    Result<Void> deleteCategory(Integer id);
}
