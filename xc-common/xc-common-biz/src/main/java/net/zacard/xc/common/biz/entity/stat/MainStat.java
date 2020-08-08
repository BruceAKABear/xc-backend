package net.zacard.xc.common.biz.entity.stat;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-07-18 12:56
 */
@Data
public class MainStat implements Serializable {

    private static final long serialVersionUID = 164312453007209242L;

    private UserStat userStat;

    private RoleStat roleStat;

    private KeepStat keepStat;

    private PayStat payStat;

    private ArpuStat arpuStat;

    /**
     * 日志格式化，格式为：yyyy-MM-dd
     */
    private String dateFormat;

    /**
     * 冗余字段：当前日期
     */
    @JSONField(serialize = false)
    private DateTime currentDate;
}
