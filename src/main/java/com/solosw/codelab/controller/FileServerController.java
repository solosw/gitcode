package com.solosw.codelab.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;

@Controller
public class FileServerController {

    // 定义基础目录
    private static final String BASE_DIR = "C:\\Users\\solosw\\Desktop\\CodeLab\\area\\test"; // 替换为你的文件存储目录

    @GetMapping("/file/**")
    public ResponseEntity<?> getFile(@PathVariable("path") String path) {
        // 构建完整的文件路径
        String filePath = BASE_DIR + "/" + path.replaceFirst("/file/", "");
        File file = new File(filePath);

        // 检查文件是否存在且是文件
        if (!file.exists() || !file.isFile()) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

        // 设置响应头信息
        FileSystemResource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}
