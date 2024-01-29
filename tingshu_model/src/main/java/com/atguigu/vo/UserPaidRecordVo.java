package com.atguigu.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserPaidRecordVo {

    private String orderNo;
    private Long userId;
    private String itemType;
    private List<Long> itemIdList;
}
