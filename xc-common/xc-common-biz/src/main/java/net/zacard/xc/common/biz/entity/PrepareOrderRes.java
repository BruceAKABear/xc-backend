package net.zacard.xc.common.biz.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-06 10:58
 */
@Data
public class PrepareOrderRes implements Serializable {

    private static final long serialVersionUID = 1546867018985386657L;

    private String appid;

    private String nonceStr;

    private String packageStr;

    /**
     * 秒数
     */
    private String timeStamp;

    private String paySign;

    private String signType;
}


