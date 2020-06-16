package net.zacard.xc.common.biz.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.biz.util.Constant;
import net.zacard.xc.common.biz.util.EncryptUtil;
import net.zacard.xc.common.biz.util.NetUtils;
import net.zacard.xc.common.biz.util.RandomStringUtil;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一下单（生成预付订单）请求参数
 *
 * @author guoqw
 * @since 2020-06-04 21:30
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@JacksonXmlRootElement(localName = "xml")
@Slf4j
public class UnifiedOrderReq implements Serializable {

    private static final long serialVersionUID = -697913190916221708L;

    /**
     * 小程序ID
     */
    @NotBlank
    @JacksonXmlProperty(localName = "appid")
    private String appId;

    /**
     * 商户号
     */
    @NotBlank
    @JacksonXmlProperty(localName = "mch_id")
    private String mchId;

    /**
     * 设备号
     */
    @JacksonXmlProperty(localName = "device_info")
    private String deviceInfo;

    /**
     * 随机字符串
     */
    @NotBlank
    @JacksonXmlProperty(localName = "nonce_str")
    private String nonceStr;

    /**
     * 签名
     */
    @NotBlank
    private String sign;

    /**
     * 签名类型，默认MD5
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "sign_type")
    private String signType;

    /**
     * 商品描述,规范格式：熙辰游戏-{商品名称}
     */
    @NotBlank
    private String body;

    /**
     * 商品详情,商品详细描述，对于使用单品优惠的商户，该字段必须按照规范上传
     */
    @JacksonXmlCData
    private String detail;

    /**
     * 附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用
     */
    @JacksonXmlCData
    private String attach;

    /**
     * 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*且在同一个商户号下唯一
     */
    @NotBlank
    @JacksonXmlProperty(localName = "out_trade_no")
    private String outTradeNo;

    /**
     * 符合ISO 4217标准的三位字母代码，默认人民币：CNY
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "fee_type")
    private String feeType;

    /**
     * 订单总金额，单位为分
     */
    @Min(value = 0)
    @NotNull
    @JacksonXmlProperty(localName = "total_fee")
    private Integer totalFee;

    /**
     * 支持IPV4和IPV6两种格式的IP地址。调用微信支付API的机器IP
     */
    @NotBlank
    @JacksonXmlProperty(localName = "spbill_create_ip")
    private String spbillCreateIp;

    /**
     * 订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010
     */
    @JacksonXmlProperty(localName = "time_start")
    private String timeStart;

    /**
     * 单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。
     * 订单失效时间是针对订单号而言的，由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "time_expire")
    private String timeExpire;

    /**
     * 订单优惠标记，使用代金券或立减优惠功能时需要的参数
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "goods_tag")
    private String goodsTag;

    /**
     * 异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数
     */
    @NotBlank
//    @JacksonXmlCData
    @JacksonXmlProperty(localName = "notify_url")
    private String notifyUrl;

    /**
     * 交易类型,小程序取值如下：JSAPI
     */
    @NotBlank
    @JacksonXmlProperty(localName = "trade_type")
    private String tradeType;

    /**
     * 商品ID,trade_type=NATIVE时，此参数必传。此参数为二维码中包含的商品ID，商户自行定义
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "product_id")
    private String productId;

    /**
     * 指定支付方式,上传此参数no_credit--可限制用户不能使用信用卡支付
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "limit_pay")
    private String limitPay;

    /**
     * 用户标识,trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识
     */
    @JacksonXmlProperty(localName = "openid")
    private String openid;

    /**
     * 电子发票入口开放标识
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "receipt")
    private String receipt;

    /**
     * 场景信息
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "scene_info")
    private String sceneInfo;

    public static UnifiedOrderReq buildForMiniProgram(MiniProgramConfig miniProgramConfig, String body, int totalFee, String openid) {
        Map<String, String> signMap = new HashMap<>();
        UnifiedOrderReq req = new UnifiedOrderReq();
        req.setAppId(miniProgramConfig.getAppId());
        signMap.put("appid", req.getAppId());

        req.setMchId(miniProgramConfig.getMchId());
        signMap.put("mch_id", req.getMchId());

        req.setDeviceInfo("MINI-PROGRAM");
        signMap.put("device_info", req.getDeviceInfo());

        req.setNonceStr(RandomStringUtil.getRandomUpperString());
        signMap.put("nonce_str", req.getNonceStr());

        req.setBody(body);
        signMap.put("body", req.getBody());

        req.setOutTradeNo(RandomStringUtil.orderId());
        signMap.put("out_trade_no", req.getOutTradeNo());

        req.setTotalFee(totalFee);
        signMap.put("total_fee", req.getTotalFee().toString());

        // TODO 注意这里可能获取到的本地ip为null，需要配置服务器
        req.setSpbillCreateIp(NetUtils.getLocalHost());
        signMap.put("spbill_create_ip", req.getSpbillCreateIp());

        req.setTimeStart(DateTime.now().toString(Constant.TRADE_START_TIME_FORMAT));
        signMap.put("time_start", req.getTimeStart());

        req.setNotifyUrl(Constant.NOTIFY_URL);
        signMap.put("notify_url", req.getNotifyUrl());

        req.setTradeType(Constant.TRADE_TYPE_MINI_PROGRAM);
        signMap.put("trade_type", req.getTradeType());

        req.setOpenid(openid);
        signMap.put("openid", req.getOpenid());

        // 计算签名
        req.setSign(EncryptUtil.wxPaySign(signMap, miniProgramConfig.getKey()));
        return req;
    }

    /**
     * 转成xml字符串
     */
    public String xml() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String xml = null;
        try {
            xml = xmlMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("统一下单req转为xml报错", e);
        }
//        log.info("xml:" + xml);
        return xml;
    }

}
