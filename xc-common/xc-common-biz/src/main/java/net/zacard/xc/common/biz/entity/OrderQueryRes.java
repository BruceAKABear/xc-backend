package net.zacard.xc.common.biz.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import net.zacard.xc.common.biz.infra.validator.WxSign;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

/**
 * @author guoqw
 * @since 2020-06-13 11:31
 */
@WxSign
@Data
@JacksonXmlRootElement(localName = "xml")
public class OrderQueryRes extends WxCommonRes {

    private static final long serialVersionUID = -5563244634170953248L;

    @NotBlank(message = "openid不能为空")
    @JacksonXmlCData
    private String openid;

    /**
     * 用户是否关注公众账号，Y-关注，N-未关注
     */
    @NotBlank(message = "is_subscribe不能为空")
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "is_subscribe")
    private String isSubscribe;

    /**
     * 交易类型，取值为：JSAPI，NATIVE，APP等
     */
    @NotBlank(message = "trade_type不能为空")
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "trade_type")
    private String tradeType;

    /**
     * 交易状态:
     * SUCCESS—支付成功
     * <p>
     * REFUND—转入退款
     * <p>
     * NOTPAY—未支付
     * <p>
     * CLOSED—已关闭
     * <p>
     * REVOKED—已撤销（刷卡支付）
     * <p>
     * USERPAYING--用户支付中
     * <p>
     * PAYERROR--支付失败(其他原因，如银行返回失败)
     */
    @NotBlank(message = "trade_state不能为空")
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "trade_state")
    private String tradeState;

    /**
     * 银行类型，采用字符串类型的银行标识
     */
    @NotBlank(message = "bank_type不能为空")
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "bank_type")
    private String bankType;

    /**
     * 订单总金额，单位为分
     */
    @NotNull(message = "total_fee不能为空")
    @JacksonXmlProperty(localName = "total_fee")
    private Integer totalFee;

    /**
     * 应结订单金额=订单金额-非充值代金券金额，应结订单金额<=订单金额
     */
    @JacksonXmlProperty(localName = "settlement_total_fee")
    private Integer settlementTotalFee;

    /**
     * 货币种类
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "fee_type")
    private String feeType;

    /**
     * 现金支付金额
     */
    @NotNull(message = "cash_fee不能为空")
    @JacksonXmlProperty(localName = "cash_fee")
    private Integer cashFee;

    /**
     * 现金支付货币类型
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "cash_fee_type")
    private String cashFeeType;

    /**
     * 代金券金额<=订单金额，订单金额-代金券金额=现金支付金额
     */
    @JacksonXmlProperty(localName = "coupon_fee")
    private Integer couponFee;

    /**
     * 代金券使用数量
     */
    @JacksonXmlProperty(localName = "coupon_count")
    private Integer couponCount;

    /**
     * CASH--充值代金券
     * NO_CASH---非充值代金券
     * <p>
     * 并且订单使用了免充值券后有返回（取值：CASH、NO_CASH）。$n为下标,从0开始编号，举例：coupon_type_0
     * <p>
     * 注意：只有下单时订单使用了优惠，回调通知才会返回券信息。
     * 下列情况可能导致订单不可以享受优惠：可能情况。
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "coupon_type_$n")
    private String couponTypeN;

    /**
     * 代金券ID
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "coupon_id_$n")
    private String couponIdN;

    /**
     * 单个代金券支付金额,$n为下标，从0开始编号
     */
    @JacksonXmlProperty(localName = "coupon_fee_$n")
    private Integer couponFeeN;

    /**
     * 微信支付订单号
     */
    @NotBlank(message = "transaction_id")
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "transaction_id")
    private String transactionId;

    /**
     * 商户订单号
     */
    @NotBlank(message = "out_trade_no不能为空")
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "out_trade_no")
    private String outTradeNo;

    /**
     * 商家数据包，原样返回
     */
    @JacksonXmlCData
    private String attach;

    /**
     * 支付完成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010
     */
    @NotBlank(message = "time_end")
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "time_end")
    private String timeEnd;

    /**
     * 交易状态描述:对当前查询订单状态的描述和下一步操作的指引
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "trade_state_desc")
    private String tradeStateDesc;
}
