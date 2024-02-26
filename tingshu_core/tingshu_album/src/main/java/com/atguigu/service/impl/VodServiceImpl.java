package com.atguigu.service.impl;

import com.atguigu.config.VodProperties;
import com.atguigu.entity.TrackInfo;
import com.atguigu.service.VodService;
import com.atguigu.util.UploadFileUtil;
import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaRequest;
import com.tencentcloudapi.vod.v20180717.models.DescribeMediaInfosRequest;
import com.tencentcloudapi.vod.v20180717.models.DescribeMediaInfosResponse;
import com.tencentcloudapi.vod.v20180717.models.MediaInfo;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
@Service
public class VodServiceImpl implements VodService {
    @Autowired
    private VodProperties vodProperties;

    /**
     * 上传声音文件
     * @param file
     * @return
     */
    @SneakyThrows
    @Override
    public Map<String, Object> uploadTrack(MultipartFile file) {
        String tempPath = UploadFileUtil.uploadTempPath(vodProperties.getTempPath(), file);
        VodUploadClient client = new VodUploadClient(vodProperties.getSecretId(), vodProperties.getSecretKey());
        VodUploadRequest request = new VodUploadRequest();
        request.setMediaFilePath(tempPath);
        VodUploadResponse response = client.upload(vodProperties.getRegion(), request);
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("mediaFileId",response.getFileId());
        retMap.put("mediaUrl",response.getMediaUrl());
        return retMap;
    }

    @SneakyThrows
    @Override
    public void getTrackMediaInfo(TrackInfo trackInfo) {
        Credential cred = new Credential(vodProperties.getSecretId(), vodProperties.getSecretKey());
        // 实例化要请求产品的client对象,clientProfile是可选的
        VodClient client = new VodClient(cred, vodProperties.getRegion());
        // 实例化一个请求对象,每个接口都会对应一个request对象
        DescribeMediaInfosRequest req = new DescribeMediaInfosRequest();
        String[] fileIds1 = {trackInfo.getMediaFileId()};
        req.setFileIds(fileIds1);
        // 返回的resp是一个DescribeMediaInfosResponse的实例，与请求对象对应
        DescribeMediaInfosResponse resp = client.DescribeMediaInfos(req);
        if(resp.getMediaInfoSet().length>0){
            MediaInfo mediaInfo = resp.getMediaInfoSet()[0];
            trackInfo.setMediaSize(mediaInfo.getMetaData().getSize());
            trackInfo.setMediaDuration(BigDecimal.valueOf(mediaInfo.getMetaData().getDuration()));
            trackInfo.setMediaType(mediaInfo.getBasicInfo().getType());
        }
    }

    @SneakyThrows
    @Override
    public void removeTrack(String mediaFileId) {
        Credential cred = new Credential(vodProperties.getSecretId(), vodProperties.getSecretKey());        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        // 实例化要请求产品的client对象,clientProfile是可选的
        VodClient client = new VodClient(cred, vodProperties.getRegion());
        // 实例化一个请求对象,每个接口都会对应一个request对象
        DeleteMediaRequest req = new DeleteMediaRequest();
        req.setFileId(mediaFileId);
        // 返回的resp是一个DeleteMediaResponse的实例，与请求对象对应
        client.DeleteMedia(req);
    }
}


