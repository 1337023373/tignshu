package com.atguigu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author 亨亨sama
 * @since 2024-01-29
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("user_info")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 手机
     */
    private String phone;

    /**
     * 密码
     */
    private String password;

    /**
     * 微信openId
     */
    private String wxOpenId;

    /**
     * nickname
     */
    private String nickname;

    /**
     * 主播用户头像图片
     */
    private String avatarUrl;

    /**
     * 用户是否为VIP会员
     */
    private Byte isVip;

    /**
     * 当前VIP到期时间，即失效时间
     */
    private LocalDateTime vipExpireTime;

    /**
     * 性别
     */
    private Byte gender;

    /**
     * 出生年月
     */
    private LocalDate birthday;

    /**
     * 简介
     */
    private String intro;

    /**
     * 主播认证类型
     */
    private Byte certificationType;

    /**
     * 认证状态
     */
    private Byte certificationStatus;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Byte isDeleted;
}
