package com.atguigu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户vip服务记录表
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
@Getter
@Setter
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
    private LocalDateTime startTime;

    /**
     * 到期时间
     */
    private LocalDateTime expireTime;

    /**
     * 是否自动续费
     */
    private Byte isAutoRenew;

    /**
     * 下次自动续费时间
     */
    private LocalDateTime nextRenewTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Byte isDeleted;
}
