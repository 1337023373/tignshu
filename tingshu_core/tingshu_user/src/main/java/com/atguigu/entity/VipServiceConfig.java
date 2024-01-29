package com.atguigu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * vip服务配置表
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("vip_service_config")
public class VipServiceConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 服务名称
     */
    private String name;

    /**
     * 原价，单位元，用于营销展示
     */
    private BigDecimal price;

    /**
     * 折后价，单位元，即实际价格
     */
    private BigDecimal discountPrice;

    /**
     * 优惠简介
     */
    private String intro;

    /**
     * 服务简介，富文本
     */
    private String richIntro;

    /**
     * 服务月数
     */
    private Integer serviceMonth;

    /**
     * 服务图片url
     */
    private String imageUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Byte isDeleted;
}
