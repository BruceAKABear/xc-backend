package net.zacard.xc.common.biz.entity.stat;

import lombok.Data;

import java.io.Serializable;

/**
 * 留存相关统计
 *
 * @author guoqw
 * @since 2020-07-17 21:27
 */
@Data
public class KeepStat implements Serializable {

    private static final long serialVersionUID = -1367062950376341204L;

    /**
     * 次日留存
     */
    private double keep2Day;

    /**
     * 3日留存
     */
    private double keep3Day;

    /**
     * 7日留存
     */
    private double keep7Day;
}
