package net.zacard.xc.common.biz.entity.stat;

import lombok.Data;

import java.io.Serializable;

/**
 * 支付统计
 *
 * @author guoqw
 * @since 2020-07-18 11:43
 */
@Data
public class PayStat implements Serializable {

    private static final long serialVersionUID = 8320871167788887591L;

    /**
     * 新增支付人数
     */
    private long newPayUsers;

    /**
     * 总支付人数
     */
    private long totalPayUsers;

    /**
     * 总支付次数
     */
    private long totalPayCount;

    /**
     * 新增支付金额
     */
    private double newPaySum;

    /**
     * 总支付金额
     */
    private double totalPaySum;
}
