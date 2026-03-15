package com.ikun.blogsystem.service.impl;

import com.ikun.blogsystem.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    // 定义上传文件的根目录，这里我们暂时硬编码
    // 生产环境中，这个路径通常会配置在 application.yml 中
    private final String uploadDir = "C:/Users/li195/Desktop/img";

    private String save(MultipartFile file, String folderName) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传的文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                fileExtension = originalFilename.substring(dotIndex);
            }
        }

        String uniqueFilename = UUID.randomUUID() + fileExtension;
        Path folderPath = Paths.get(uploadDir, folderName);
        Path destPath = folderPath.resolve(uniqueFilename);

        try {
            Files.createDirectories(folderPath);
            file.transferTo(destPath);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }

        return "/uploads/" + folderName + "/" + uniqueFilename;
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        return save(file, "avatars");
    }

    @Override
    public String uploadBlogImage(MultipartFile file) {
        return save(file, "blog");
    }
}
