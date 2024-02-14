package com.atguigu.service;

import com.atguigu.entity.TrackInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface VodService {
    Map<String, Object> uploadTrack(MultipartFile file);

    void getTrackMediaInfo(TrackInfo trackInfo);

    void removeTrack(String mediaFileId);
}
