//package com.sky.config;
//
//import com.sky.properties.QiniuProperties;
//import com.sky.utils.QiniuUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//
//@Slf4j
//@Configuration
//public class QiniuConfiguration {
//
//    @Resource
//    private  QiniuUtil qiniuUtil;
//    @Bean
//    public QiniuUtil qiniuUtil() {
//
//        // 检查是否为空
//        if (qiniuProperties.getAccessKey() == null || qiniuProperties.getAccessKey().trim().isEmpty()) {
//            throw new IllegalArgumentException("Qiniu accessKey is empty!");
//        }
//        if (qiniuProperties.getSecretKey() == null || qiniuProperties.getSecretKey().trim().isEmpty()) {
//            throw new IllegalArgumentException("Qiniu secretKey is empty!");
//        }
//
//        return new QiniuUtil(
//                qiniuProperties.getAccessKey(),
//                qiniuProperties.getSecretKey(),
//                qiniuProperties.getBucket(),
//                qiniuProperties.getDomain()
//        );
//    }
//}