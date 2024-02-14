package com.atguigu.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jodd.io.FileNameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;


@Configuration
@EnableConfigurationProperties(MinIOConfigProperties.class)
public class MinIOConfig {
    //    注入配置类

    @Autowired
    private MinIOConfigProperties minioConfigProperties;
    @Autowired
    private MinioClient minioClient;



    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(minioConfigProperties.getEndpoint())
                .credentials(minioConfigProperties.getAccessKey(), minioConfigProperties.getSecretKey())
                .build();
//        是否存在这个桶
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfigProperties.getBucketName()).build());
        if (!found) {
//            minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioConfigProperties.getBucketName()).build());
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioConfigProperties.getBucketName()).build());
        } else {
            System.out.println("Bucket " + minioConfigProperties.getBucketName() + " already exists.");
        }
        return minioClient;
    }

// MultipartFile  接收使用多种请求方式来进行上传文件的代表形式,文件上传
    public String uploadFile(MultipartFile file) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
// getOriginalFilename 返回客户端文件系统中的原始文件名。
        String originalFilename = file.getOriginalFilename();

//        两个方法都可以获取文件的后缀名,前者使用substring的截断形式,把.之后的后缀名拿到,
//        后者方法是获取文件的后缀名
        //originalFilename.substring(originalFilename.lastIndexOf("."));
        String extension = FileNameUtil.getExtension(originalFilename);
        String fileName = uuid +"."+ extension;
        //通过文件格式上传一个文件   这里把原文档中的uploadObject方法改成了putObject,把文件路径改成了stream流
//        之前的uploadObject,是通过路径上传文件
        minioClient.putObject(PutObjectArgs.builder()
                        .bucket(minioConfigProperties.getBucketName())
//                        上传的文件名字,因为不确定文件名字,所以这里使用uuid+文件本身的名字,避免重复
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
//       上传成功后,需要返回一个地址,提供文件下载,就在minio的下载选项
        String url = minioConfigProperties.getEndpoint() + "/" + minioConfigProperties.getBucketName() + "/" + fileName;
        return url;
    }
}