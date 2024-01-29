package com.atguigu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户vip服务记录表
 * </p>
 *
 * @author zhangqiang
 * @since 2023-10-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_vip_info")
public class UserVipInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 开始生效日期
     */
    private Date startTime;

    /**
     * 到期时间
     */
    private Date expireTime;

    /**
     * 是否自动续费
     */
    private Integer isAutoRenew;

    /**
     * 下次自动续费时间
     */
    private Date nextRenewTime;

    /**
     * 创建时间
     */
    private Date createTime;

    private Date updateTime;

    private Integer isDeleted;


}
