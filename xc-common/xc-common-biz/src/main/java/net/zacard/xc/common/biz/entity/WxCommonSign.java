package net.zacard.xc.common.biz.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-13 13:28
 */
@Data
public class WxCommonSign implements Serializable {

    private static final long serialVersionUID = -5897205184170727946L;

    @NotBlank(message = "appid不能为空")
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "appid")
    private String appId;

    @NotBlank(message = "return_code不能为空")
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "return_code")
    private String returnCode;

    /**
     * 签名
     */
    @NotBlank(message = "sign不能为空")
    @JacksonXmlCData
    private String sign;

    /**
     * 默认MD5
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "sign_type")
    private String signType;
}
