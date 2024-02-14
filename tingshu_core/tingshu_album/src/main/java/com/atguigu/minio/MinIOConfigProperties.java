package com.atguigu.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


//创建配置类,把配置文件的属性注入当前bean中
@Data
@ConfigurationProperties(prefix = "minio")
public class MinIOConfigProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
