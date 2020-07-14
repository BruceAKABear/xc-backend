package net.zacard.xc.website.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author guoqw
 * @since 2020-07-13 19:50
 */
@Data
public class Blog implements Serializable {

    private static final long serialVersionUID = -1638408398568177353L;

    private String title;
    private String user_id;
    private String area;
    private String phone;
    private String tab;
    private String img;
    private String text;
    private Date create_time;

}
