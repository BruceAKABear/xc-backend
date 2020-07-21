package net.zacard.xc.common.api.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-13 16:40
 */
@Data
public class RoleInfoDto implements Serializable {

    private static final long serialVersionUID = -246020532118102995L;

    @NotBlank(message = "userToken不能为空")
    private String userToken;

    /**
     * 操作类型：CREATE-创建角色|UPDATE-角色升级
     */
    @NotBlank(message = "type不能为空")
    private String type;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String name;

    /**
     * 区服名
     */
    @NotBlank(message = "area不能为空")
    private String area;

    /**
     * 角色等级
     */
    @NotBlank(message = "level不能为空")
    private String level;

    /**
     * 角色虚拟货币数量
     */
    @NotNull(message = "money不能为空")
    private Integer money;

    @NotBlank(message = "sign不能为空")
    private String sign;
}
