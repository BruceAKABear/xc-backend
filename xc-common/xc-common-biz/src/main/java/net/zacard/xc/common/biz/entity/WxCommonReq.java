package net.zacard.xc.common.biz.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * wx支付请求wx服务器的通用参数
 *
 * @author guoqw
 * @since 2020-06-13 11:32
 */
@Data
public class WxCommonReq implements Serializable {

    private static final long serialVersionUID = -3214445811449120300L;

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
     * 随机字符串
     */
    @NotBlank(message = "nonce_str不能为空")
    @JacksonXmlProperty(localName = "nonce_str")
    private String nonceStr;

    /**
     * 签名
     */
    @NotBlank(message = "sign不能为空")
    private String sign;

    @JacksonXmlProperty(localName = "sign_type")
    private String signType;
}
