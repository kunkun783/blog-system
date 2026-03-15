package com.ikun.blogsystem.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 上传头像文件
     *
     * @param file 头像文件
     * @return 文件的访问 URL
     */
    String uploadAvatar(MultipartFile file);

    String uploadBlogImage(MultipartFile file);
}
