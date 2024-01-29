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
 * 用户已付款声音
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("user_paid_track")
public class UserPaidTrack implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 专辑id
     */
    private Long albumId;

    /**
     * 声音id
     */
    private Long trackId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Byte isDeleted;
}
