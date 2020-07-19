package net.zacard.xc.common.biz.entity.stat;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author guoqw
 * @since 2020-07-18 12:44
 */
@Data
public class BaseStat implements Serializable {

    private static final long serialVersionUID = -3059786889368215347L;

    /**
     * 日志格式化，格式为：yyyy-MM-dd
     */
    private String dateFormat;

    private Date date;
}
