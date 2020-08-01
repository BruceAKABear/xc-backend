package net.zacard.xc.common.biz.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

/**
 * @author guoqw
 * @since 2020-06-13 11:39
 */
@Data
public class WxCommonRes extends WxCommonSign {

    private static final long serialVersionUID = -7755232950651431121L;

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


//    /**
//     * 签名
//     */
//    @JacksonXmlCData
//    private String sign;

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
}
