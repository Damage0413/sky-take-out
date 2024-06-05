package com.sky.controller.admin;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sky.properties.AliOssProperties;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@Api(tags = "文件上传接口")
public class Upload {
    // @Autowired
    // private AliOssUtil aliOssUtil;

    @Autowired
    private AliOssProperties aliOssProperties;

    @PostMapping("/admin/common/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传，{}", file);
        AliOssUtil aliOssUtil = new AliOssUtil(aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(),
                aliOssProperties.getAccessKeySecret(), aliOssProperties.getBucketName());
        try {
            String filename = file.getOriginalFilename();
            String extention = filename.substring(filename.lastIndexOf("."));
            String objectname = UUID.randomUUID().toString() + extention;
            String uploadpath = aliOssUtil.upload(file.getBytes(), objectname);
            return Result.success(uploadpath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.error("上传失败");
    }
}
