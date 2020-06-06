package net.zacard.xc.common.biz.util;

import java.util.regex.Pattern;

/**
 * @author guoqw
 * @since 2020-06-05 14:17
 */
public class Constant {

    public static final Pattern POINT_SPLIT_PATTERN = Pattern.compile("\\s*[.]+\\s*");

    public static final String ORDER_GENERTE_TIME_FORMAT = "yyyyMMddHHmmssSSS";

    public static final String TRADE_START_TIME_FORMAT = "yyyyMMddHHmmss";

    public static final String NOTIFY_URL = "http://www.xichengame.com/api/apy/callback";

    public static final String TRADE_TYPE_MINI_PROGRAM = "JSAPI";

    /**
     * 统一下单地址
     */
    public static final String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    public static final String CODE_TO_SESSION_UR = "https://api.weixin.qq.com/sns/jscode2session";

}
