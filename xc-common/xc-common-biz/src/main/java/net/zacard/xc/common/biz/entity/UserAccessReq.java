package net.zacard.xc.common.biz.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-08-08 17:39
 */
@Data
public class UserAccessReq implements Serializable {

    private static final long serialVersionUID = 7921063234593421473L;

    @NotBlank(message = "userToken不能为空")
    private String userToken;
}
