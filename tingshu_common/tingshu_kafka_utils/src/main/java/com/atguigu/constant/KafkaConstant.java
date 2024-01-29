package com.atguigu.constant;

public class KafkaConstant {

    /**
     * 专辑
     */
    public static final String ONSALE_ALBUM_QUEUE = "tingshu.album.upper";
    public static final String OFFSALE_ALBUM_QUEUE = "tingshu.album.lower";
    public static final String UPDATE_ALBUM_BUY_NUM_QUEUE = "tingshu.album.stat.update";
    public static final String QUEUE_ALBUM_ES_STAT_UPDATE = "tingshu.album.es.stat.update";
    public static final String QUEUE_ALBUM_RANKING_UPDATE = "tingshu.album.ranking.update";

    /**
     * 声音
     */
    public static final String UPDATE_TRACK_STAT_QUEUE = "tingshu.track.stat.update";

    /**
     * 取消订单
     */
    //延迟取消订单队列
    public static final String QUEUE_ORDER_CANCEL  = "tingshu.queue.order.cancel";
    //取消订单 延迟时间 单位：秒
    public static final int DELAY_TIME  = 5*60;

    /**
     * 支付
     */
    public static final String PAY_ORDER_SUCCESS_QUEUE = "tingshu.order.pay.success";
    public static final String RECHARGE_SUCCESS_QUEUE = "tingshu.recharge.pay.success";


    /**
     * 账户
     */
    public static final String UNLOCK_ACCOUNT_QUEUE = "tingshu.account.unlock";
    public static final String DEDUCT_LOCK_ACCOUNT_QUEUE = "tingshu.account.minus";

    /**
     * 用户
     */
    public static final String USER_PAID_QUEUE = "tingshu.user.pay.record";
    public static final String USER_REGISTER_QUEUE = "tingshu.user.register";
    public static final String QUEUE_USER_VIP_EXPIRE_STATUS = "tingshu.user.vip.expire.status";

    /**
     * 热门关键字
     */
    public static final String QUEUE_KEYWORD_INPUT  = "tingshu.keyword.input";
    public static final String QUEUE_KEYWORD_OUT  = "tingshu.keyword.out";
}
