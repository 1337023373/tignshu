package com.atguigu.minio;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FileUploader {
    public static void main(String[] args)
            throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
//                            minio运行的端口
                            .endpoint("http://192.168.157.166:9000")
//                            输入账号密码
                            .credentials("enjoy6288", "enjoy6288")
                            .build();

            // Make 'asiatrip' bucket if not exist.
//            是否存在这个桶,
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("henghengsama0130").build());
            if (!found) {
                // Make a new bucket called 'asiatrip'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("henghengsama0130").build());
            } else {
                System.out.println("Bucket 'henghengsama0130' already exists.");
            }

            // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
            // 'asiatrip'.
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("henghengsama0130")
                            .object("hhh.jpg")
                            .filename("D:\\Program Files\\feiq\\RecvFile\\zhangqiang\\hhh.jpg")
                            .build());
            System.out.println(
                    "文件上传成功");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        }
    }
}