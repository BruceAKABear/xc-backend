package net.zacard.xc.common.biz.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-06 11:15
 */
@Data
public class PrepareOrderReq implements Serializable {

    private static final long serialVersionUID = -6306624560296182487L;

    @NotBlank(message = "userToken不能为空")
    private String userToken;

    @Min(0)
    @NotNull
    private Integer price;

    @NotBlank(message = "itemName不能为空")
    private String itemName;

    private String itemId;

    private String other;
}
