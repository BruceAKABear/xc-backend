package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.api.entity.ContentType;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * @author guoqw
 * @since 2020-07-06 19:40
 */
@Data
@Document(collection = "content")
public class Content extends AuditDocument {

    private static final long serialVersionUID = -6602114118815758802L;

    @NotBlank(message = "infoId不能为空")
    private String infoId;

    @NotNull(message = "type不能为空")
    private ContentType type;

    @NotBlank(message = "title不能为空")
    private String title;

    /**
     * 头图url
     */
    private String headPicUrl;

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
