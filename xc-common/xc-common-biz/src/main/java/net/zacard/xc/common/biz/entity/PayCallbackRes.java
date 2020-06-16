package net.zacard.xc.common.biz.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-06 16:51
 */
@Data
@JacksonXmlRootElement(localName = "xml")
public class PayCallbackRes implements Serializable {

    private static final long serialVersionUID = 1305671544538466656L;

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "return_code")
    private String returnCode;

    @JacksonXmlCData
    @JacksonXmlProperty(localName = "return_msg")
    private String returnMsg;

    public static PayCallbackRes success() {
        PayCallbackRes payCallbackRes = new PayCallbackRes();
        payCallbackRes.setReturnCode("SUCCESS");
        return payCallbackRes;
    }

    public static PayCallbackRes fail(String message) {
        PayCallbackRes payCallbackRes = new PayCallbackRes();
        payCallbackRes.setReturnCode("FAIL");
        payCallbackRes.setReturnMsg(message);
        return payCallbackRes;
    }
}
