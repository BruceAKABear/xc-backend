package net.zacard.xc.common.biz.infra.mongo;

import lombok.Data;

/**
 * @author guoqw
 * @since 2020-06-01 15:53
 */
@Data
public class AuditDocument extends BaseDocument {

    private static final long serialVersionUID = -8569737796622727473L;

    private String createUserId;

    private String createUserName;

    private String updateUserId;

    private String updateUserName;
}
