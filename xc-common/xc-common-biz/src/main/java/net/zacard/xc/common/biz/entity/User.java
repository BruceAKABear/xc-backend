package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author guoqw
 * @since 2020-06-04 07:42
 */
@Data
public class User extends AuditDocument {

    private static final long serialVersionUID = 6139982282864360468L;

    @Indexed(unique = true)
    public String openid;

    public String nickName;

    /**
     * 0：未知、1：男、2：女
     */
    public int sex;

    /**
     * 用户头像url
     */
    public String avatarUrl;

    /**
     * 城市
     */
    public String city;

    /**
     * 省
     */
    public String province;

    /**
     * 国家
     */
    public String country;

    public String unionId;
}
