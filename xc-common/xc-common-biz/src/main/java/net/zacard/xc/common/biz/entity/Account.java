package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author guoqw
 * @since 2020-06-21 14:39
 */
@Data
@Document(collection = "account")
public class Account extends AuditDocument {

    private String name;

    /**
     * 0：未知、1：男、2：女
     */
    private int sex;
}
