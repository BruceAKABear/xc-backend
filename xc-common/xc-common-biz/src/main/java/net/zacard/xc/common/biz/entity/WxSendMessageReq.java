package net.zacard.xc.common.biz.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-27 17:15
 */
@Data
public class WxSendMessageReq implements Serializable {

    private static final long serialVersionUID = -4005256096894113591L;

    /**
     * 用户的 OpenID
     */
    private String touser;

    /**
     * 消息类型:
     * text	文本消息
     * image	图片消息
     * link	图文链接
     * miniprogrampage	小程序卡片
     */
    private String msgtype;

    /**
     * 文本消息，msgtype="text" 时必填
     */
    private Text text;

    /**
     * 图片消息，msgtype="image" 时必填
     */
    private Image image;

    /**
     * 图文链接，msgtype="link" 时必填
     */
    private Link link;

    /**
     * 小程序卡片，msgtype="miniprogrampage" 时必填
     */
    private Miniprogrampage miniprogrampage;

    public static WxSendMessageReq text(String openid, String content) {
        WxSendMessageReq req = new WxSendMessageReq();
        req.setTouser(openid);
        req.setMsgtype("text");
        Text text = new Text();
        text.setContent(content);
        req.setText(text);
        return req;
    }

    public static WxSendMessageReq link(String openid) {
        WxSendMessageReq req = new WxSendMessageReq();
        req.setTouser(openid);
        req.setMsgtype("link");

        // TODO
        Link link = new Link();

        return req;
    }

    public static WxSendMessageReq mini(String openid, String title, String pagepath, String thumbMediaId) {
        WxSendMessageReq req = new WxSendMessageReq();
        req.setTouser(openid);
        req.setMsgtype("miniprogrampage");

        Miniprogrampage miniprogrampage = new Miniprogrampage();
        miniprogrampage.setTitle(title);
        miniprogrampage.setPagepath(pagepath);
        miniprogrampage.setThumb_media_id(thumbMediaId);

        req.setMiniprogrampage(miniprogrampage);
        return req;
    }

    @Data
    private static class Text {
        private String content;
    }

    @Data
    private static class Image {

        /**
         * 发送的图片的媒体ID，通过 新增素材接口 上传图片文件获得。
         * https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/customer-message/customerServiceMessage.uploadTempMedia.html
         */
        private String media_id;
    }

    @Data
    private static class Link {

        /**
         * 标题
         */
        private String title;

        /**
         * 图文链接消息
         */
        private String description;

        /**
         * 图文链接消息被点击后跳转的链接
         */
        private String url;

        /**
         * 图文链接消息的图片链接，支持 JPG、PNG 格式，较好的效果为大图 640 X 320，小图 80 X 80
         */
        private String thumb_url;
    }

    @Data
    private static class Miniprogrampage {

        /**
         * 消息标题
         */
        private String title;

        /**
         * 小程序的页面路径，跟app.json对齐，支持参数，比如pages/index/index?foo=bar
         */
        private String pagepath;

        /**
         * 小程序消息卡片的封面， image 类型的 media_id，通过 新增素材接口 上传图片文件获得，建议大小为 520*416
         */
        private String thumb_media_id;
    }
}
