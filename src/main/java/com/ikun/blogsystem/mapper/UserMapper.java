package com.ikun.blogsystem.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ikun.blogsystem.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
