package net.zacard.xc.common.api.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-05 22:00
 */
@Data
public class UserDto implements Serializable {

    private static final long serialVersionUID = 743979705175171366L;

    @NotBlank(message = "渠道id不能为空")
    private String channelId;

    @NotBlank(message = "openid不能为空")
    public String openid;

    @NotBlank(message = "appId不能为空")
    public String appId;

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
