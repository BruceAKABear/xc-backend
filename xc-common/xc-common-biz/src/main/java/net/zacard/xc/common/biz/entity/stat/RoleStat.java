package net.zacard.xc.common.biz.entity.stat;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-07-17 21:25
 */
@Data
public class RoleStat implements Serializable {

    private static final long serialVersionUID = -6968977351901240379L;

    /**
     * 新增角色数量
     */
    private long newRole;

    /**
     * 总共角色数量
     */
    private long totalRole;
}
