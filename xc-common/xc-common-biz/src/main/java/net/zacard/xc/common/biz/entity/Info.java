package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.api.entity.Content;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * 咨询页
 *
 * @author guoqw
 * @since 2020-07-06 19:46
 */
@Data
@Document(collection = "info")
public class Info extends AuditDocument {

    private static final long serialVersionUID = -4968599342054864558L;

    private String name;

    /**
     * 咨询页内容列表
     */
    private List<Content> contents;

    /**
     * 备注
     */
    private String remark;
}
