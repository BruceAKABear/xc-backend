package net.zacard.xc.common.biz.entity.stat;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户相关的统计
 *
 * @author guoqw
 * @since 2020-07-17 21:23
 */
@Data
public class UserStat implements Serializable {

    private static final long serialVersionUID = -5700508963482965482L;

    /**
     * 新增用户数
     */
    private long newUser;

    /**
     * 总用户数
     */
    private long totalUser;

}
