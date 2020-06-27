package net.zacard.xc.common.biz.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-27 16:21
 */
@Data
public class AccessTokenRes implements Serializable {

    private static final long serialVersionUID = 6494211539902538203L;

    /**
     * 获取到的凭证
     */
    private String access_token;

    /**
     * 凭证有效时间，单位：秒。目前是7200秒之内的值。
     */
    private Long expires_in;

    /**
     * 错误码
     */
    private Long errcode;

    /**
     * 错误信息
     */
    private String errmsg;
}
