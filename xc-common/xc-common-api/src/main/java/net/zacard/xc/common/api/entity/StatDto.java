package net.zacard.xc.common.api.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-07-18 12:59
 */
@Data
public class StatDto implements Serializable {

    private static final long serialVersionUID = -3191141687147398455L;

    @NotNull(message = "统计数据的开始时间不能为空")
    private Long start;

    @NotNull(message = "统计数据的结束时间不能为空")
    private Long end;

    private String channelId;
}
