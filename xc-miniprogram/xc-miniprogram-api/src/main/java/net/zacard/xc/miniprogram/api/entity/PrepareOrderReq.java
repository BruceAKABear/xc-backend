package net.zacard.xc.miniprogram.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-05 20:25
 */
@Data
public class PrepareOrderReq implements Serializable {

    private static final long serialVersionUID = -2759050373380419316L;

    private String channelId;

    private String userToken;

    private int price;

    /**
     * 渠道上生成的订单id
     */
    private String channelOrderId;

    /**
     * 类名id
     */
    private String itemId;

    /**
     * 附加参数，支付成功会带上该参数回调渠道商接口
     */
    private String other;


    //========测试参数

    private String openid;

    private String appId;

}
