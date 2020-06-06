package net.zacard.xc.common.biz.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一下单响应
 *
 * @author guoqw
 * @since 2020-06-05 21:29
 */
@Data
@JacksonXmlRootElement(localName = "xml")
public class UnifiedOrderRes implements Serializable {

    private static final long serialVersionUID = -3925080109048063465L;

    /**
     * 返回状态码.SUCCESS/FAIL
     * <p>
     * 此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "return_code")
    private String returnCode;

    /**
     * 返回信息，如非空，为错误原因
     * <p>
     * 签名失败
     * <p>
     * 参数格式校验错误
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "return_msg")
    private String returnMsg;

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "appid")
    private String appId;

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "mch_id")
    private String mchId;

    /**
     * 设备号
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "device_info")
    private String deviceInfo;

    /**
     * 随机字符串
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "nonce_str")
    private String nonceStr;


    /**
     * 签名
     */
    @JacksonXmlCData
    private String sign;

    /**
     * 业务结果:SUCCESS/FAIL
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "result_code")
    private String resultCode;

    /**
     * 错误代码
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "err_code")
    private String errCode;

    /**
     * 错误信息描述
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "err_code_des")
    private String errCodeDes;

    /**
     * 交易类型，取值为：JSAPI，NATIVE，APP等
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "trade_type")
    private String tradeType;

    /**
     * 预支付交易会话标识:微信生成的预支付会话标识，用于后续接口调用中使用，该值有效期为2小时
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "prepay_id")
    private String prepayId;

    /**
     * trade_type=NATIVE时有返回，此url用于生成支付二维码，然后提供给用户进行扫码支付
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "code_url")
    private String codeUrl;
}
