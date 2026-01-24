//package com.sky.controller.admin;
//
//import com.sky.result.Result;
//import com.sky.utils.QiniuUtil;
//import io.swagger.annotations.Api;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.lang.annotation.Retention;
//import java.util.UUID;
//
//import static java.util.UUID.randomUUID;
//
//@RestController
//@RequestMapping("admin/common")
//@Slf4j
//@Api(tags = "文件上传")
//public class CommonController {
//    @Autowired
//    private QiniuUtil qiniuUtil;
//
//    @PostMapping("/upload")
//    public Result<String> upload( MultipartFile multipartFile){
//        String originalFilename = multipartFile.getOriginalFilename();
//        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
//        String new_file = UUID.randomUUID() + substring;
//        try {
//            qiniuUtil.upload(multipartFile.getBytes(),new_file);
//        } catch (IOException e) {
//            log.info("异常"+e);
//            throw new RuntimeException(e);
//        }
//        return Result.success(new_file);
//    }
//}
package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.QiniuUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags = "文件上传")
public class CommonController {

    @Autowired
    private QiniuUtil qiniuUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(@RequestParam("file") MultipartFile multipartFile) {
        log.info("文件上传开始");

        // 安全检查必须放在最前面！
        if (multipartFile == null || multipartFile.isEmpty()) {
            log.error("上传文件为空");
            return Result.error("上传文件不能为空");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        log.info("原始文件名：{}", originalFilename);

        if (originalFilename == null || originalFilename.isEmpty()) {
            return Result.error("文件名不能为空");
        }

        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return Result.error("文件格式错误，缺少扩展名");
        }

        String extension = originalFilename.substring(lastDotIndex);
        String newFileName = UUID.randomUUID().toString() + extension;

        try {
            newFileName = qiniuUtil.upload(multipartFile.getBytes(), newFileName);
            log.info("文件上传成功：{}", newFileName);
            return Result.success(newFileName);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage());
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}