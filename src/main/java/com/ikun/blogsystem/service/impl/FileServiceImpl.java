package com.ikun.blogsystem.service.impl;

import com.ikun.blogsystem.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    // 定义上传文件的根目录，这里我们暂时硬编码
    // 生产环境中，这个路径通常会配置在 application.yml 中
    private final String uploadDir = "C:/Users/li195/Desktop/img";

    @Override
    public String uploadAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传的文件不能为空");
        }

        try {
            // 确保上传目录存在
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // 保存文件
            File dest = new File(uploadDir + uniqueFilename);
            file.transferTo(dest);

            // 返回可访问的 URL
            return "/uploads/avatars/" + uniqueFilename;

        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }
}
