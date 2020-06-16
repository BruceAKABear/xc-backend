package net.zacard.xc.common.biz.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-13 15:38
 */
@Data
public class PayQueryRes implements Serializable {

    private static final long serialVersionUID = -1027998726678253283L;

    private String code;

    private String message;

    private String state;

    private Integer price;

    private String openid;

    private String time;

    private String channelOrderId;

    private String sign;

    public static PayQueryRes fail(String message) {
        PayQueryRes res = new PayQueryRes();
        res.setCode("500");
        res.setMessage(message);
        return res;
    }

    public static PayQueryRes ok() {
        PayQueryRes res = new PayQueryRes();
        res.setCode("200");
        return res;
    }
}
