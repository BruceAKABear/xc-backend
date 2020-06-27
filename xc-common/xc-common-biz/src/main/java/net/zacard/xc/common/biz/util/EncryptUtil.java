package net.zacard.xc.common.biz.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 加密解密工具类
 *
 * @author guoqw
 * @since 2020-06-05 12:54
 */
@Slf4j
public class EncryptUtil {

    /**
     * 微信支付，对于给定字段签名
     *
     * @param target    对象必须都是基本类型、基本类型包装类、String
     * @param secretKey 私钥
     */
    public static String wxPaySign(Object target, String secretKey) {
        Map<String, String> signMap = ObjectUtil.objectToMapNonNull(target);
        // 去除sign字段(sign本身不参与签名)
        signMap.remove("sign");
        return wxPaySign(signMap, secretKey);
    }

    /**
     * 微信支付，对于给定字段签名
     */
    public static String wxPaySign(Map<String, String> signMap, String secretKey) {
        List<String> keys = new ArrayList<>(signMap.keySet());
        // key排序
        Collections.sort(keys);
        // 拼接
        StringBuilder sign = new StringBuilder(128);
        for (String key : keys) {
            sign.append(key)
                    .append("=")
                    .append(signMap.get(key))
                    .append("&");
        }
        // 拼接key
        sign.append("key=").append(secretKey);
        log.debug("拼接的签名字符串：" + sign.toString());
        // md5 & upper
        return md5(sign.toString()).toUpperCase();
    }

    public static String md5(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5实例初始化失败", e);
        }
        byte[] byteArray = messageDigest.digest();

        return bufferToHex(byteArray);
    }

    public static String wxMessageCheckSign(String token, String timestamp, String nonce) {
        List<String> keys = Lists.newArrayList(token, timestamp, nonce);
        // key排序
        Collections.sort(keys);
        // 拼接
        String sign = String.join("", keys);
        return sha1(sign);
    }

    public static String sha1(String str) {
        // SHA1签名生成
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw ExceptionUtil.unchecked(e);
        }
        md.update(str.getBytes());
        byte[] digest = md.digest();
        StringBuilder hexstr = new StringBuilder();
        String shaHex;
        for (int i = 0; i < digest.length; i++) {
            shaHex = Integer.toHexString(digest[i] & 0xFF);
            if (shaHex.length() < 2) {
                hexstr.append(0);
            }
            hexstr.append(shaHex);
        }
        return hexstr.toString();
    }

    private static String bufferToHex(byte[] byteArray) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                builder.append('0').append(
                        Integer.toHexString(0xFF & byteArray[i]));
            } else {
                builder.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }
        return builder.toString();
    }
}
