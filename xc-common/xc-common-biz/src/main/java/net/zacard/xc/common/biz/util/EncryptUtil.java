package net.zacard.xc.common.biz.util;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

    private static final String AES_KEY = "AES";

    private static final String DEFAULT_SECRET_KEY = "XC2020CX0202SSSS";

    /**
     * 驼峰转下划线
     */
    private static final Converter<String, String> CONVERTER = CaseFormat.LOWER_CAMEL.converterTo(
            CaseFormat.LOWER_UNDERSCORE);

    /**
     * 微信支付，对于给定字段签名
     *
     * @param target    对象必须都是基本类型、基本类型包装类、String
     * @param secretKey 私钥
     */
    public static String wxPaySign(Object target, String secretKey) {
        return wxPaySign(target, secretKey, true);
    }

    public static String wxPaySign(Object target, String secretKey, boolean needConvert) {
        Map<String, String> signMap = ObjectUtil.objectToMapNonNull(target);
        return wxPaySign(signMap, secretKey, needConvert);
    }

    public static String wxPaySign(Map<String, String> signMap, String secretKey, boolean needConvert) {
        // 去除sign字段(sign本身不参与签名)
        signMap.remove("sign");
        // 把key从驼峰转为下划线
        if (needConvert) {
            Map<String, String> convertMap = new HashMap<>(signMap.size());
            for (Map.Entry<String, String> entry : signMap.entrySet()) {
                convertMap.put(CONVERTER.convert(entry.getKey()), entry.getValue());
            }
            signMap = convertMap;

            // 将app_id统一替换成appid
            if (signMap.containsKey("app_id")) {
                String value = signMap.remove("app_id");
                signMap.put("appid", value);
            }
        }
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

    private static byte[] hexToBuffer(String hex) {
        if (hex == null) {
            return null;
        }
        int l = hex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2),
                    16);
        }
        return b;
    }

    public static String aesEncrypt(String src) {
        return aesEncrypt(src, DEFAULT_SECRET_KEY);
    }

    public static String aesDecrypt(String src) {
        return aesDecrypt(src, DEFAULT_SECRET_KEY);
    }

    public static String aesEncrypt(String src, String key) {
        byte[] encrypted;
        try {
            if (key == null || key.length() != 16) {
                throw new Exception("key不满足条件");
            }
            byte[] raw = key.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, AES_KEY);
            Cipher cipher = Cipher.getInstance(AES_KEY);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encrypted = cipher.doFinal(src.getBytes());
        } catch (Exception e) {
            log.error("加密失败!", e);
            throw ExceptionUtil.unchecked(e);
        }
        return bufferToHex(encrypted);
    }

    public static String aesDecrypt(String src, String key) {
        byte[] original = new byte[0];
        try {
            if (key == null || key.length() != 16) {
                throw new Exception("key不满足条件");
            }
            byte[] raw = key.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, AES_KEY);
            Cipher cipher = Cipher.getInstance(AES_KEY);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = hexToBuffer(src);
            original = cipher.doFinal(encrypted1);
        } catch (Exception e) {
            log.error("解密失败!", e);
            throw ExceptionUtil.unchecked(e);
        }
        return new String(original);
    }

}
