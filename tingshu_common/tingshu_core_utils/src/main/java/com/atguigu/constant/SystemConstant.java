package com.atguigu.constant;

public class SystemConstant {

    //专辑付费类型  0101-免费、0102-vip免费、0103-付费
    public static final String FREE_ALBUM ="0101";  // 免费
    public static final String VIPFREE_ALBUM ="0102";  // vip免费
    public static final String NEED_PAY_ALBUM ="0103";  // 付费

    //专辑价格类型 0201-单集 0202-整专辑
    public static final String  ALBUM_PRICE_TYPE_ONE="0201";  // 单级
    public static final String  ALBUM_PRICE_TYPE_ALL="0202";  // 整专辑

    //专辑状态 0301-审核通过 0302-审核不通过
    public static final String ALBUM_APPROVED ="0301";  // 审核通过
    public static final String  ALBUM_STATUS_NO_PASS="0302";  // 审核不通过

    //专辑统计 0401-播放量 0402-订阅量 0403-购买量 0403-评论数
    public static final String PLAY_NUM_ALBUM ="0401";  // 播放量
    public static final String SUBSCRIBE_NUM_ALBUM ="0402";  // 订阅量
    public static final String BUY_NUM_ALBUM ="0403";  // 购买量
    public static final String COMMENT_NUM_ALBUM ="0404";  // 评论数

    //声音状态 0501-审核通过 0502"-审核不通过
    public static final String TRACK_APPROVED ="0501";  // 审核通过
    public static final String  TRACK_STATUS_NO_PASS="0502";  // 审核不通过

    //声音来源 0601-用户原创 0602-上传
    public static final String  TRACK_SOURCE_USER="0601";  // 用户原创
    public static final String  TRACK_SOURCE_UPLOAD="0602";  // 上传

    // 声音统计 0701-播放量 0702-收藏量 0703-点赞量 0704-评论数
    public static final String PLAY_NUM_TRACK ="0701";  // 播放量
    public static final String COLLECT_NUM_TRACK ="0702";  // 收藏量
    public static final String PRAISE_NUM_TRACK ="0703";  // 点赞量
    public static final String COMMENT_NUM_TRACK ="0704";  // 评论数

    //用户状态 0801-正常 0802-锁定
    public static final String  USER_STATUS_NORMAL="0801";  // 正常
    public static final String  USER_STATUS_LOCK="0802";  // 锁定

    //订单状态 0901-正常 0902-已支付 0903-已取消
    public static final String ORDER_UNPAID ="0901";  // 未支付
    public static final String ORDER_PAID ="0902";  // 已支付
    public static final String  ORDER_STATUS_CANCEL="0903";  // 已取消

    //订单付款项目类型 1001-专辑 1002-声音 1003-vip会员
    public static final String BUY_ALBUM ="1001";  // 专辑
    public static final String BUY_TRACK ="1002";  // 声音
    public static final String BUY_VIP ="1003";  // vip会员

    //订单支付方式 1101-微信 1102-支付宝 1103-账户余额
    public static final String  ORDER_PAY_WAY_WEIXIN="1101";  // 微信
    public static final String  ORDER_PAY_WAY_ALIPAY="1102";  // 支付宝
    public static final String ACCOUNT_BALANCES ="1103";  // 账户余额

    //账号交易类型 1201-充值 1202-锁定 1203-解锁 1204-消费
    public static final String  ACCOUNT_TRADE_TYPE_DEPOSIT="1201";  // 充值
    public static final String  ACCOUNT_TRADE_TYPE_LOCK="1202";  // 锁定
    public static final String UNLOCK_ACCOUNT ="1203";  // 解锁
    public static final String ACCOUNT_EXPENSE ="1204";  // 消费

    //支付类型 1301-订单 1302-充值
    public static final String PAY_ORDER ="1301";  // 订单
    public static final String  PAYMENT_TYPE_RECHARGE="1302";  // 充值

    //支付表支付状态 1401-未支付 1402-已支付
    public static final String PAYMENT_UNPAID ="1401";  // 未支付
    public static final String  PAYMENT_STATUS_PAID="1402";  // 已支付

    //订单减免类型 1405-专辑折扣 1406-VIP服务折
    public static final String ALBUM_DISCOUNT ="1405";  // 专辑折扣
    public static final String  ORDER_DERATE_VIP_SERVICE_DISCOUNT="1406";  // VIP服务折扣

    public static final String OPEN_ALBUM="1";
    public static final String PRIVATE_ALBUM="0";

}
