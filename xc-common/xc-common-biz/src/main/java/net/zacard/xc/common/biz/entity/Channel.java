package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 渠道
 *
 * @author guoqw
 * @since 2020-06-04 07:49
 */
@Data
@Document
public class Channel extends AuditDocument {

    private static final long serialVersionUID = -5458208023699420152L;

    /**
     * 渠道名称
     */
    @NotBlank
    private String name;

    @NotBlank
    private String miniProgramConfigId;

    /**
     * 渠道对接的小程序appid
     */
    @NotBlank
    private String appId;

    /**
     * 给渠道分配的appSecret,用来做签名校验
     */
    @Indexed(unique = true)
    private String appSecret;

    /**
     * 图片url
     */
    private String picUrl;

    /**
     * 游戏的h5资源url接口
     */
    private String gameH5Url;

    /**
     * 支付回调url
     */
    @NotBlank
    private String payCallbackUrl;

    /**
     * 支付回调方法：POST|GET
     */
    private String payCallbackMethod;

    /**
     * 是否上线
     */
    private Boolean online;

}
