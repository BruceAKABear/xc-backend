package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 角色操作日志
 *
 * @author guoqw
 * @since 2020-06-13 16:35
 */
@Data
@Document(collection = "role_opt_log")
public class RoleOptLog extends AuditDocument {

    private static final long serialVersionUID = -4813025705502404685L;

    /**
     * 操作类型：CREATE-创建角色|UPDATE-角色升级
     */
    private String type;

    private String userId;

    private String openid;

    private String channelId;

}
