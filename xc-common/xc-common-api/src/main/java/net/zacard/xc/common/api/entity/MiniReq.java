package net.zacard.xc.common.api.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-21 13:41
 */
@Data
public class MiniReq implements Serializable {

    private static final long serialVersionUID = 5180601474378795331L;

    /**
     * 小程序名称，模糊匹配
     */
    private String name;

}
