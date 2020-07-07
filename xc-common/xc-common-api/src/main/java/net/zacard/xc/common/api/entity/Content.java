package net.zacard.xc.common.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-07-06 19:40
 */
@Data
public class Content implements Serializable {

    private static final long serialVersionUID = -6602114118815758802L;

    private ContentType type;

    private String title;

    /**
     * 头图
     */
    private String titlePicUrl;

    /**
     * 背景图片
     */
    private String backgroundPicUrl;

    /**
     * 点击展示的文章内容
     */
    private String article;

    /**
     * 点击展示的图片
     */
    private String picUrl;

    /**
     * 点击展示的h5资源
     */
    private String h5Url;

    /**
     * 顺序, 数字越小越靠前
     */
    private Integer order;
}
