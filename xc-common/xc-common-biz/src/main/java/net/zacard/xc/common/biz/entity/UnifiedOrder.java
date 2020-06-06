package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 统一下单
 *
 * @author guoqw
 * @since 2020-06-05 21:31
 */
@Data
@Document(collection = "unified_order_req")
public class UnifiedOrder extends AuditDocument {

    private static final long serialVersionUID = -2636988819530091478L;

    private UnifiedOrderReq req;

    private UnifiedOrderRes res;

    private String message;

    /**
     * 渠道商获取下单参数时附带的other参数，将在支付成功回调给渠道商的时候带回
     */
    private String channelOther;
}
