package com.atguigu.vod;

import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrackTest {
  //    上传声音文件
  public static void uploadMedia() {
       // public static void main(String [] args) {
        VodUploadClient client = new VodUploadClient("AKIDhL8uZ6jN8y36B8ykVJ2t1DVlaZhwyvlf", "HdvNftBQoAQZn5FI96557660qeRDwedX");
        VodUploadRequest request = new VodUploadRequest();
        request.setMediaFilePath("D:\\Program Files\\feiq\\RecvFile\\cardigan.mp3");
        try {
            VodUploadResponse response = client.upload("ap-guangzhou", request);
            log.info("Upload FileId = {}", response.getFileId());
        } catch (Exception e) {
            // 业务方进行异常处理
            log.error("Upload Err", e);
        }
    }

//    获取媒体详细信息
//    public static void getVodInfo() {
//        try{
//            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
//            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
//            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
//            Credential cred = new Credential("AKIDhL8uZ6jN8y36B8ykVJ2t1DVlaZhwyvlf", "HdvNftBQoAQZn5FI96557660qeRDwedX");
//            // 实例化一个http选项，可选的，没有特殊需求可以跳过
//            HttpProfile httpProfile = new HttpProfile();
//            httpProfile.setEndpoint("vod.tencentcloudapi.com");
//            // 实例化一个client选项，可选的，没有特殊需求可以跳过
//            ClientProfile clientProfile = new ClientProfile();
//            clientProfile.setHttpProfile(httpProfile);
//            // 实例化要请求产品的client对象,clientProfile是可选的
//            VodClient client = new VodClient(cred, "ap-guangzhou", clientProfile);
//            // 实例化一个请求对象,每个接口都会对应一个request对象
//            DescribeMediaInfosRequest req = new DescribeMediaInfosRequest();
//            String[] fileIds1 = {"388912588482336776"};
//            req.setFileIds(fileIds1);
//
//            // 返回的resp是一个DescribeMediaInfosResponse的实例，与请求对象对应
//            DescribeMediaInfosResponse resp = client.DescribeMediaInfos(req);
//            // 输出json格式的字符串回包
//            System.out.println(DescribeMediaInfosResponse.toJsonString(resp));
//        } catch (TencentCloudSDKException e) {
//            System.out.println(e.toString());
//        }
//    }

    //    删除声音信息,这段代码在腾讯云上
    //public static void main(String [] args) {
//        try{
//            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
//            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
//            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
//            Credential cred = new Credential("AKIDhL8uZ6jN8y36B8ykVJ2t1DVlaZhwyvlf", "HdvNftBQoAQZn5FI96557660qeRDwedX");
//            // 实例化一个http选项，可选的，没有特殊需求可以跳过
//            HttpProfile httpProfile = new HttpProfile();
//            httpProfile.setEndpoint("vod.tencentcloudapi.com");
//            // 实例化一个client选项，可选的，没有特殊需求可以跳过
//            ClientProfile clientProfile = new ClientProfile();
//            clientProfile.setHttpProfile(httpProfile);
//            // 实例化要请求产品的client对象,clientProfile是可选的
//            VodClient client = new VodClient(cred, "ap-guangzhou", clientProfile);
//            // 实例化一个请求对象,每个接口都会对应一个request对象
//            DeleteMediaRequest req = new DeleteMediaRequest();
//            req.setFileId("388912588482336776");
//            // 返回的resp是一个DeleteMediaResponse的实例，与请求对象对应
//            DeleteMediaResponse resp = client.DeleteMedia(req);
//            // 输出json格式的字符串回包
//            System.out.println(DeleteMediaResponse.toJsonString(resp));
//        } catch (TencentCloudSDKException e) {
//            System.out.println(e.toString());
//        }
//    }
}
