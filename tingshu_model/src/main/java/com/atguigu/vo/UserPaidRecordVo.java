package com.atguigu.vo;

import lombok.Data;

import java.util.List;

@Data
public class UserPaidRecordVo {

    private String orderNo;
    private Long userId;
    private String itemType;
//    购买的类型id集合
    private List<Long> itemIdList;
}
