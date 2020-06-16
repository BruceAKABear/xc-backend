package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author guoqw
 * @since 2020-06-06 15:58
 */
@Data
@Document
public class Trade extends AuditDocument {

    private static final long serialVersionUID = -3977239654486611547L;

    /**
     * 系统生成的orderId
     */
    @Indexed(unique = true)
    private String orderId;

    /**
     * 微信订单号
     */
    @Indexed(unique = true)
    private String transactionId;

    /**
     * 渠道方生成的订单id
     */
    private String channelOrderId;

    /**
     * 渠道方携带的other参数
     */
    private String channelOther;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 交易状态
     */
    private String tradeState;

    /**
     * 订单完成时间
     */
    private Date endTime;

    /**
     * 订单失败的code
     */
    private String errorCode;

    /**
     * 订单失败的信息
     */
    private String errorMessage;

    /**
     * 订单总金额,单位：分
     */
    private int totalFee;

    /**
     * 商品名称
     */
    private String itemName;

    private String itemId;

    private String channelId;

    private String appId;

    private String userId;

    private String openid;

    private String userToken;

    private String mchId;

    /**
     * 是否发送过回调给渠道方
     */
    private Boolean hasSendCallback;
}
