package com.atguigu.controller;

import com.atguigu.minio.MinIOConfig;
import com.atguigu.result.RetVal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件上传接口")
@RestController
@RequestMapping("/api/album")
public class FileUploadController {

    @Autowired
    private MinIOConfig minIOConfig;
    @Operation(summary = "图片上传")
    @PostMapping("/fileUpload")
    public RetVal fileUpload(@RequestBody MultipartFile file) throws Exception {
        String url = minIOConfig.uploadFile(file);
        return RetVal.ok(url);
    }
}
