package net.zacard.xc.website.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-07-03 20:23
 */
@Data
public class Response implements Serializable {

    private static final long serialVersionUID = 7923463627462071147L;

    private Integer code;

    private String msg;

    public static Response ok() {
        Response res = new Response();
        res.setCode(200);
        return res;
    }
}
