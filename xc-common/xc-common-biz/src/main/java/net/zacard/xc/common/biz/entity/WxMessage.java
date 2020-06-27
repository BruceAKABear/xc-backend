package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
import net.zacard.xc.common.biz.util.BeanMapper;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 收到的用户微信消息
 *
 * @author guoqw
 * @since 2020-06-27 18:16
 */
@Data
@Document(collection = "wx_message")
public class WxMessage extends AuditDocument {

    private static final long serialVersionUID = -4396740684063769571L;

    /**
     * 发送消息的用户openid
     */
    private String openid;

    /**
     * 接收消息的小程序appId
     */
    private String receiveAppId;

    /**
     * 消息创建时间
     */
    private Date messageCreateTime;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 文本消息内容
     */
    private String content;

    /**
     * 消息id，64位整型
     */
    private Long msgId;

    /**
     * 图片链接（由系统生成）
     * 当msgType=image的时候有值
     */
    private String picUrl;

    /**
     * 图片消息媒体id，可以调用[获取临时素材]((getTempMedia)接口拉取数据
     * 当msgType=image的时候有值
     */
    private String mediaId;

    /**
     * 标题
     * 当msgType=miniprogrampage的时候有值
     */
    private String title;

    /**
     * 小程序appid
     * 当msgType=miniprogrampage的时候有值
     */
    private String appId;

    /**
     * 小程序页面路径
     * 当msgType=miniprogrampage的时候有值
     */
    private String pagePath;

    /**
     * 封面图片的临时cdn链接
     * 当msgType=miniprogrampage的时候有值
     */
    private String thumbUrl;

    /**
     * 封面图片的临时素材id
     * 当msgType=miniprogrampage的时候有值
     */
    private String thumbMediaId;

    /**
     * 事件类型，user_enter_tempsession
     * 用户在小程序“客服会话按钮”进入客服会话
     */
    private String event;

    /**
     * 开发者在客服会话按钮设置的 session-from 属性
     */
    private String sessionFrom;

    public static WxMessage build(WxMessageReq wxMessageReq) {
        WxMessage wxMessage = BeanMapper.map(wxMessageReq, WxMessage.class);
        wxMessage.setReceiveAppId(wxMessageReq.getToUserName());
        wxMessage.setOpenid(wxMessageReq.getFromUserName());
        wxMessage.setMessageCreateTime(new Date(wxMessageReq.getCreateTime() * 1000));
        return wxMessage;
    }
}
