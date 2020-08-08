package net.zacard.xc.common.biz.entity.stat;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-08-08 16:55
 */
@Data
public class ArpuStat implements Serializable {

    private static final long serialVersionUID = -6491134290382733904L;

    /**
     * 当天支付总金额，单位:分
     * 对应：付费
     */
    private long currentPayAmount;

    /**
     * 总支付金额，单位:分
     * 对应：总额
     */
    private long totalPayAmount;

    /**
     * 付费率:总付费人数除以总创角
     */
    private double payRate;
}
