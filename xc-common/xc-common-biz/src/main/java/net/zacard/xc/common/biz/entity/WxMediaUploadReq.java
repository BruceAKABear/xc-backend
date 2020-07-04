package net.zacard.xc.common.biz.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-27 21:19
 */
@Data
public class WxMediaUploadReq implements Serializable {

    private static final long serialVersionUID = 8193717069472390008L;

    private Long errcode;

    private String errmsg;

    private String type;

    /**
     * 媒体文件上传后，获取标识，3天内有效
     */
    private String media_id;

    /**
     * 媒体文件上传时间戳
     */
    private Long created_at;
}
