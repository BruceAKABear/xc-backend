package net.zacard.xc.manage.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;

/**
 * 资讯配置
 *
 * @author guoqw
 * @since 2020-06-21 15:22
 */
@Data
public class Info extends AuditDocument {

    private static final long serialVersionUID = -3393615434792730976L;

    private String name;

    private String title;

    /**
     * article：文章|pic：图片|list:列表|game：游戏
     */
    private String type;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 图片url
     */
    private String picUrl;

    /**
     * 头图url
     */
    private String headPicUrl;

    /**
     * 背景图url
     */
    private String backPicUrl;
}
