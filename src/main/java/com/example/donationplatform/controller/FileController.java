package com.example.donationplatform.controller;

import com.example.donationplatform.entity.Result;
import com.example.donationplatform.util.AliOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file) throws IOException {
        if (file == null){
        return Result.error("请选择文件");
       }
        String[] split = file.getOriginalFilename().split("\\.");
        String suffix = split[split.length - 1].toLowerCase();
        List<String> allow = List.of("jpg", "png", "jpeg");
        if (!allow.contains(suffix)) {
            throw new RuntimeException("支持 jpg/png/jpeg,请不要上传木马,维护网络安全,人人有责!不要用你的技术，挑战我的底线!");
        }
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String folder="projects-picture/";
        String StoreName = folder + fileName;
        String url = aliOssUtil.upload(file.getBytes(), StoreName);
        return Result.success(url);
    }
}
