package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * 小程序配置
 *
 * @author guoqw
 * @since 2020-06-05 17:20
 */
@Document(collection = "mini_program_config")
@Data
public class MiniProgramConfig extends AuditDocument {

    private static final long serialVersionUID = 5707267486118169427L;

    /**
     * 小程序名称
     */
    private String name;

    /**
     * 小程序id
     */
    @Indexed(unique = true)
    private String appId;

    /**
     * 商户id
     */
    private String mchId;

    /**
     * 商户平台设置的密钥key
     */
    private String key;

    /**
     * 小程序承接的渠道商列表
     */
    private List<String> channelIds;
}
