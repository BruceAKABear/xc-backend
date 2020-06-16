package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
    @NotBlank(message = "name不能为空")
    private String name;

    /**
     * 小程序id
     */
    @NotBlank(message = "appid不能为空")
    @Indexed(unique = true)
    private String appId;

    /**
     * 商户id
     */
    @NotBlank(message = "mch_id不能为")
    private String mchId;

    /**
     * 商户平台设置的密钥key
     */
    @NotBlank(message = "key不能为空")
    private String key;

//    /**
//     * 小程序承接的渠道商列表
//     */
//    private List<String> channelIds;
}
