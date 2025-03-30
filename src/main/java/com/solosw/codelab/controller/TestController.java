package com.solosw.codelab.controller;

import com.solosw.codelab.annations.PermissionCheck;
import com.solosw.codelab.controller.base.BaseController;
import com.solosw.codelab.entity.bo.ResponseBo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/back/files")
public class TestController extends BaseController {

    @Value("${local.file.dir}")
    private String localFilePath;
    @Value("${local.file.path}")
    private String getLocalFilePath;
    @PostMapping("/upload")
    public ResponseBo upLoadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 校验文件是否为空
            if (file.isEmpty()) {
                return ResponseBo.getFail("上传文件不能为空");
            }

            // 安全处理文件名：去除路径信息防止路径遍历攻击
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            // 创建目标目录（如果不存在）
            File uploadDir = new File(localFilePath);
            if (!uploadDir.exists()) {

                return ResponseBo.getFail("无法创建存储目录");
            }
            String uniqueFileName = UUID.randomUUID() + "_" + fileName;
            File targetFile = new File(uploadDir, uniqueFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            return ResponseBo.getSuccess("/back"+getLocalFilePath+"/"+uniqueFileName);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
