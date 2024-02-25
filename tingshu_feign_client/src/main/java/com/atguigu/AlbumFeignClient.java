package com.atguigu;

import com.atguigu.entity.AlbumInfo;
import com.atguigu.result.RetVal;
import com.atguigu.vo.AlbumStatVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(value = "tingshu-album",fallback = AlbumFeignClientImpl.class)
//创建远程链接
@FeignClient(value = "tingshu-album")
public interface AlbumFeignClient {
    @GetMapping("/api/album/albumInfo/getAlbumInfoById/{albumId}")
        // TODO: 2024/2/20 这里后面会有问题 写全路径
    RetVal<AlbumInfo> getAlbumInfoById(@PathVariable Long albumId);

    //  获取专辑统计信息的远程调用
    @GetMapping("/api/album/albumInfo/getAlbumStatInfo/{albumId}")
    public RetVal<AlbumStatVo> getAlbumStatInfo(@PathVariable Long albumId);
}
