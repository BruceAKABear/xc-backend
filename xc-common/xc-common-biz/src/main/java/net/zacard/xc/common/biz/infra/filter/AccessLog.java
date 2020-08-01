package net.zacard.xc.common.biz.infra.filter;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口访问输入输出日志
 *
 * @author guoqw
 * @since 2020-06-06 16:04
 */
@Data
public class AccessLog implements Serializable {

    private static final long serialVersionUID = -2745351979676210014L;

    @JSONField(ordinal = 0)
    private Boolean success;

    @JSONField(ordinal = 1)
    private String httpStatus;

    @JSONField(ordinal = 2)
    private String method;

    @JSONField(ordinal = 3)
    private String clientIP;

    /**
     * 耗时：毫秒
     */
    @JSONField(ordinal = 4)
    private Long time;

    @JSONField(ordinal = 5)
    private String uri;

    @JSONField(ordinal = 6)
    private String parameters;

    @JSONField(ordinal = 7)
    private String response;

    @JSONField(ordinal = 8)
    private String other;

    @JSONField(ordinal = 9)
    private String requestTime;

    @JSONField(ordinal = 10)
    private String responseTime;

    @JSONField(ordinal = 11)
    private String exception;

}
