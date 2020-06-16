package net.zacard.xc.common.biz.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author guoqw
 * @since 2020-06-09 20:53
 */
public final class SystemPropertyUtil {

    private static final Logger log = LoggerFactory.getLogger(SystemPropertyUtil.class);

    private SystemPropertyUtil() {
    }

    public static String get(String key) {
        return System.getProperty(key);
    }

    public static String get(String key, String defValue) {
        return System.getProperty(key, defValue);
    }

    public static int getInt(String key) {
        return Integer.parseInt(System.getProperty(key));
    }

    public static int getInt(String key, int defValue) {
        String value = System.getProperty(key);
        if (value == null) {
            return defValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("读取配置出错", e);
            return defValue;
        }
    }

    public static long getLong(String key) {
        return Long.parseLong(System.getProperty(key));
    }

    public static long getLong(String key, long defValue) {
        String value = System.getProperty(key);
        if (value == null) {
            return defValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            log.warn("读取配置出错", e);
            return defValue;
        }
    }

    public static double getDouble(String key) {
        return Double.parseDouble(System.getProperty(key));
    }

    public static double getDouble(String key, double defValue) {
        String value = System.getProperty(key);
        if (value == null) {
            return defValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            log.warn("读取配置出错", e);
            return defValue;
        }
    }
}
