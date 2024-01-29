package com.atguigu.entity;

import lombok.Data;

@Data
public class FromUser {

    //用户id
    private Long userId;
    //昵称
    private String nickname;
    //头像地址
    private String avatarUrl;
}
