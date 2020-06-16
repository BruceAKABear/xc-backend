package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.util.EncryptUtil;

import java.io.Serializable;

/**
 * 向渠道方发起的回调req
 *
 * @author guoqw
 * @since 2020-06-09 20:54
 */
@Data
public class ChannelCallbackReq implements Serializable {

    private static final long serialVersionUID = 3544666187940512070L;

    private String channelId;

    private String openid;

    private String userToken;

    private String channelOrderId;

    private Integer price;

    private String other;

    private String itemId;

    private String itemName;

    private String sign;

    public static ChannelCallbackReq build(Trade trade) {
        ChannelCallbackReq req = new ChannelCallbackReq();
        req.setChannelId(trade.getChannelId());
        req.setChannelOrderId(trade.getChannelOrderId());
        req.setOpenid(trade.getOpenid());
        req.setUserToken(trade.getUserToken());
        req.setPrice(trade.getTotalFee());
        req.setItemName(trade.getItemName());
        req.setOther(trade.getChannelOther());
        req.setItemId(trade.getItemId());
        return req;
    }

    public void createSign(String appSecret) {
        this.setSign(EncryptUtil.wxPaySign(this, appSecret));
    }
}
