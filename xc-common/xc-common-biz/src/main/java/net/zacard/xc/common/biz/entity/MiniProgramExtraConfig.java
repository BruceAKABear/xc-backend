package net.zacard.xc.common.biz.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 小程序附加配置，不涉及敏感信息(例如appId、secret、token等)
 *
 * @author guoqw
 * @since 2020-06-27 20:14
 */
@Data
public class MiniProgramExtraConfig implements Serializable {

    private static final long serialVersionUID = 1803162636710500615L;

    /**
     * 请求支付跳转的小程序pagePath
     */
    private String reqPayPagePath;

    /**
     * 响应支付跳转的小程序pagePath
     */
    private String resPayPagePath;

    /**
     * 支付跳转的小程序标题
     */
    private String payTitle;

    /**
     * 支付跳转的小程序消息卡片的封面
     */
    private String payThumbMediaId;

    /**
     * 本地保存的媒体文件(图片)id
     */
    private String payThumbMediaLocalId;
}
