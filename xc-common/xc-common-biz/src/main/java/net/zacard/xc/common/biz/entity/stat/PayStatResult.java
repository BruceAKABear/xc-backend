package net.zacard.xc.common.biz.entity.stat;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-08-08 15:52
 */
@Data
public class PayStatResult implements Serializable {

    private static final long serialVersionUID = 7906324978815461654L;

    private String openid;

    /**
     * 用户支付数量
     */
    private long count;

    /**
     * 用户总支付金额，单位：分
     */
    private long amount;
}
