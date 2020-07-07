package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
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
    @NotBlank(message = "name不能为空")
    private String name;

    /**
     * 小程序id
     */
    @NotBlank(message = "appid不能为空")
    @Indexed(unique = true)
    private String appId;

    /**
     * 小程序密钥
     */
    @NotBlank(message = "appSecret不能为空")
    private String appSecret;

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

    /**
     * 设置服务器接收消息设置的token
     */
    private String messageToken;

    /**
     * 设置的消息aes key
     */
    private String messageEncodingAESKey;

    /**
     * 从wx服务器获取的小程序access_token
     */
    private String accessToken;

    /**
     * access_token的过期时间,单位秒
     */
    private Long accessTokenExpiresIn;

    /**
     * access_token刷新时间
     */
    private Date accessTokenRefreshTime;

    /**
     * 小程序承接的展示的渠道商列表
     */
    private List<String> channelIds;

    /**
     * 小程序版本
     */
    private String version;

    /**
     * 小程序展示的类型：game-游戏|info-资讯
     * 默认展示info资讯
     */
    private String showType;

    /**
     * 咨询id
     */
    private String infoId;

    /**
     * 其他配置
     */
    private MiniProgramExtraConfig extraConfig;
}
