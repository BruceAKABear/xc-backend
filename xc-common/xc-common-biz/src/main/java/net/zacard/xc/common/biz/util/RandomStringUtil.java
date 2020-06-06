package net.zacard.xc.common.biz.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 随机字符生成工具类
 *
 * @author guoqw
 * @since 2020-06-05 13:00
 */
public class RandomStringUtil extends org.apache.commons.lang3.RandomStringUtils {

    //默认随机生成的uuuid长度
    private static final int DEFAULT_UUUID_LENGTH = 32;

    public static String generateRandomCharAndNumber(int count) {
        if (count < 2) {
            throw new IllegalArgumentException(
                    "Requested random string length " + count
                            + " is less than 1.");
        }
        int numberCount = RandomUtils.nextInt(count - 1);
        numberCount = numberCount == 0 ? 1 : numberCount;
        StringBuilder stringBuilder = new StringBuilder();
        // 生成随机数字
        stringBuilder.append(randomNumeric(numberCount));
        // 生成随机字符(剔除'l','o')
        stringBuilder.append(random(count - numberCount,
                "abcdefghijkmnpqrstuvwxyz"));
        // 打乱
        char[] c = stringBuilder.toString().toCharArray();
        List<Character> lst = new ArrayList<>();
        for (int i = 0; i < c.length; i++) {
            lst.add(c[i]);
        }
        Collections.shuffle(lst);
        stringBuilder = new StringBuilder();
        for (Character character : lst) {
            stringBuilder.append(character);
        }
        return stringBuilder.toString();
    }

    public static String getRandomString() {
        return getRandomString(DEFAULT_UUUID_LENGTH);
    }

    public static String getRandomUpperString() {
        return getRandomString(DEFAULT_UUUID_LENGTH, Boolean.TRUE);
    }

    public static String getRandomString(int length) {
        return getRandomString(length, Boolean.FALSE);
    }

    public static String getRandomString(int length, Boolean upper) {
        StringBuilder builder = new StringBuilder();
        String uuid = getUUID();
        if (uuid.length() >= length) {
            builder.append(uuid.substring(0, length));
        } else {
            builder.append(uuid);
            Random random = new Random();
            while (builder.length() < length) {
                int i = random.nextInt(36);
                builder.append(i > 25 ? (char) ('0' + (i - 26)) : (char) ('a' + i));
            }
        }
        if (upper) {
            return builder.toString().toUpperCase();
        }
        return builder.toString();
    }

    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }

    private static int index = 0;
    private static String lastTime;

    /**
     * 生成订单号(27位)，纯数字，时间上有序，单调递增，最大支持每毫秒9999笔订单
     * 生成逻辑：
     * (1) 固定17位日期时间（例如2020-06-05 12：38：56.123 -> 20200605123856123）
     * (2) 固定机器ip后6位（例如10.100.158.10->158010）
     * (3) 固定4位递增数字
     *
     * @return 订单号
     */
    public static synchronized String orderId() {
        StringBuilder orderId = new StringBuilder(27);
        String nowTime = DateTime.now().toString(Constant.ORDER_GENERTE_TIME_FORMAT);
        String host = NetUtils.getLocalHost();
        // 获取host后6位（不够用0补全）
        String[] hostArray = Constant.POINT_SPLIT_PATTERN.split(host);
        // 递增数字
        if (!nowTime.equals(lastTime)) {
            lastTime = nowTime;
            index = 0;
        } else {
            // 同一毫秒数，数字递增
            index += 1;
            if (index > 9999) {
                // 等到下个毫秒
                while ((nowTime = DateTime.now().toString(Constant.ORDER_GENERTE_TIME_FORMAT)).equals(lastTime)) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException ignore) {
                    }
                }
                lastTime = nowTime;
                index = 0;
            }
        }
        orderId.append(nowTime)
                .append(StringUtils.leftPad(hostArray[2], 3, "0"))
                .append(StringUtils.leftPad(hostArray[3], 3, "0"))
                .append(StringUtils.leftPad(index + "", 4, "0"));
        return orderId.toString();
    }

}
