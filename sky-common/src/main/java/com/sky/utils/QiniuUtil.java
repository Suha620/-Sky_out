package com.sky.utils;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Data
@Slf4j
public class QiniuUtil {  // 移除 @Component 注解

//    private String accessKey;
//    private String secretKey;
//    private String bucket;
//    private String domain;
    String accessKey = "J3pNS50yeSYK1WBXxWiCModqfClb785_cbiP7H6D";
    String secretKey = "h4BLgLHhNf3EOI6RnFzqBdDRmmGzRQrxKIKY4TQ8";
    String bucket = "sky-take-out-0001";
    String domain = "http://t8s8ujxp1.hn-bkt.clouddn.com";

    public String upload(byte[] data, String fileName) {
        Configuration cfg = new Configuration(Region.autoRegion());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);

        try {
            uploadManager.put(data, fileName, upToken);
            return domain + "/" + fileName;
        } catch (QiniuException e) {
            e.printStackTrace();
            throw new RuntimeException("七牛云上传失败", e);
        }
    }
}



