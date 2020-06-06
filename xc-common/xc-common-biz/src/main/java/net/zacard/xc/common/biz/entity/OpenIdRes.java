package net.zacard.xc.common.biz.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-06 13:50
 */
@Data
public class OpenIdRes implements Serializable {

    private static final long serialVersionUID = 5448526588432179830L;

    private String appId;

    private String openid;

    private String session_key;

    private String unionid;

    private String errcode;

    private String errmsg;
}
