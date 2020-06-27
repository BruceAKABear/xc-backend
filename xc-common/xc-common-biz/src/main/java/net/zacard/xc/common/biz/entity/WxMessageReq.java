package net.zacard.xc.common.biz.entity;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-27 15:15
 */
@Data
@JacksonXmlRootElement(localName = "xml")
public class WxMessageReq implements Serializable {

    private static final long serialVersionUID = 7026994143126222458L;

    /**
     * 小程序的原始ID
     */
    @NotBlank(message = "ToUserName不能为空")
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "ToUserName")
    private String toUserName;

    /**
     * 发送者的openid
     */
    @NotBlank(message = "FromUserName不能为空")
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "FromUserName")
    private String fromUserName;

    /**
     * 消息创建时间(整型）
     */
    @NotNull(message = "CreateTime不能为空")
    @JacksonXmlProperty(localName = "CreateTime")
    private Long createTime;

    /**
     * 消息类型：text,image,miniprogrampage,event
     */
    @NotBlank(message = "MsgType不能为空")
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "MsgType")
    private String msgType;

    /**
     * 文本消息内容
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Content")
    private String content;

    /**
     * 消息id，64位整型
     */
    @JacksonXmlProperty(localName = "MsgId")
    private Long msgId;

    /**
     * 图片链接（由系统生成）
     * 当msgType=image的时候有值
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "PicUrl")
    private String picUrl;

    /**
     * 图片消息媒体id，可以调用[获取临时素材]((getTempMedia)接口拉取数据
     * 当msgType=image的时候有值
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "MediaId")
    private String mediaId;

    /**
     * 标题
     * 当msgType=miniprogrampage的时候有值
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Title")
    private String title;

    /**
     * 小程序appid
     * 当msgType=miniprogrampage的时候有值
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "AppId")
    private String appId;

    /**
     * 小程序页面路径
     * 当msgType=miniprogrampage的时候有值
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "PagePath")
    private String pagePath;

    /**
     * 封面图片的临时cdn链接
     * 当msgType=miniprogrampage的时候有值
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "ThumbUrl")
    private String thumbUrl;

    /**
     * 封面图片的临时素材id
     * 当msgType=miniprogrampage的时候有值
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "ThumbMediaId")
    private String thumbMediaId;

    /**
     * 事件类型，user_enter_tempsession
     * 用户在小程序“客服会话按钮”进入客服会话
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "Event")
    private String event;

    /**
     * 开发者在客服会话按钮设置的 session-from 属性
     */
    @JacksonXmlCData
    @JacksonXmlProperty(localName = "SessionFrom")
    private String sessionFrom;
}
