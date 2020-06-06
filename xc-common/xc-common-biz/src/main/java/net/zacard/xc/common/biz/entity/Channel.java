package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
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
    public String name;

    public String miniProgramConfigId;

    /**
     * 渠道对接的小程序appid
     */
    public String appId;

    /**
     * 支付回调url
     */
    public String payCallbackUrl;

    /**
     * 支付回调方法：POST|GET
     */
    public String payCallbackMethod;

}
