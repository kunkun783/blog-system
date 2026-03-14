package com.ikun.blogsystem.controller;

import com.ikun.blogsystem.common.result.Result;
import com.ikun.blogsystem.entity.Category;
import com.ikun.blogsystem.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有博文类别
     */
    @GetMapping({"/list", "/type/list", "/types"})
    public Result<List<Category>> list() {
        return categoryService.getAllCategories();
    }
}
