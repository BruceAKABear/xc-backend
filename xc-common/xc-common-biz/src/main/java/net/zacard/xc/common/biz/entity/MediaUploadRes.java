package net.zacard.xc.common.biz.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-07-11 12:52
 */
@Data
public class MediaUploadRes implements Serializable {

    private static final long serialVersionUID = -3492812705678236238L;

    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 媒体文件上传后，获取标识,3天有效
     */
    private String media_id;

    /**
     * 文件上传的时间戳
     */
    private String created_at;
}
