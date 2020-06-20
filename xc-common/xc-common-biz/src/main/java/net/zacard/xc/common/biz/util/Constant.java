package net.zacard.xc.common.biz.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * @author guoqw
 * @since 2020-06-05 14:17
 */
public class Constant {

    public static final Pattern POINT_SPLIT_PATTERN = Pattern.compile("\\s*[.]+\\s*");

    public static final String ORDER_GENERTE_TIME_FORMAT = "yyyyMMddHHmmssSSS";

    public static final String TRADE_START_TIME_FORMAT = "yyyyMMddHHmmss";

    public static final String NOTIFY_URL = "http://www.xichengame.com/api/pay/wx/callback";

    public static final String TRADE_TYPE_MINI_PROGRAM = "JSAPI";

    public static final String CODE_SUCCESS = "SUCCESS";

    /**
     * 统一下单地址
     */
    public static final String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 查询订单地址
     */
    public static final String ORDER_QUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";

    public static final String CODE_TO_SESSION_UR = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 回调间隔，单位毫秒
     */
    public static final long[] CALLLBACK_INTERVAL = new long[]{0, 3000, 10000, 60000};

    /**
     * 一个无聊的循环累加标示
     */
    public static final AtomicInteger INDEX = new AtomicInteger(0);

}