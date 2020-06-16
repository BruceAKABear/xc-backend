package net.zacard.xc.common.biz.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import net.zacard.xc.common.biz.util.EncryptUtil;
import net.zacard.xc.common.biz.util.RandomStringUtil;
import net.zacard.xc.common.biz.util.XmlUtil;

/**
 * @author guoqw
 * @since 2020-06-13 10:31
 */
@Data
@JacksonXmlRootElement(localName = "xml")
public class OrderQueryReq extends WxCommonReq {

    private static final long serialVersionUID = -1666652411349499389L;

    /**
     * 微信的订单号，优先使用
     */
    @JacksonXmlProperty(localName = "transaction_id")
    private String transactionId;

    /**
     * 商户订单号
     */
    @JacksonXmlProperty(localName = "out_trade_no")
    private String outTradeNo;

    public static OrderQueryReq build(Trade trade) {
        OrderQueryReq req = new OrderQueryReq();
        req.setAppId(trade.getAppId());
        req.setMchId(trade.getMchId());
        // transactionId和outTradeNo二选一，优先使用transactionId
        String transactionId = trade.getTransactionId();
        if (transactionId != null) {
            req.setTransactionId(transactionId);
        } else {
            req.setOutTradeNo(trade.getOrderId());
        }
        req.setNonceStr(RandomStringUtil.getRandomUpperString());
        return req;
    }

    /**
     * 参数签名
     */
    public void sign(String appSecret) {
        this.setSign(EncryptUtil.wxPaySign(this, appSecret));
    }

    public String xml() {
        return XmlUtil.toXml(this);
    }

}
