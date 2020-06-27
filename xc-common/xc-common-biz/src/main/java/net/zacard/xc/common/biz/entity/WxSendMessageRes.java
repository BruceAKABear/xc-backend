package net.zacard.xc.common.biz.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-27 17:28
 */
@Data
public class WxSendMessageRes  implements Serializable {

    private static final long serialVersionUID = 7829863949063742815L;

    private Long errcode;

    private String errmsg;
}
