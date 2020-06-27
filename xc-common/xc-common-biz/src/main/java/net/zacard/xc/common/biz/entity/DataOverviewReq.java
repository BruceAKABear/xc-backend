package net.zacard.xc.common.biz.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据总览
 *
 * @author guoqw
 * @since 2020-06-22 08:12
 */
@Data
public class DataOverviewReq implements Serializable {

    private static final long serialVersionUID = -5117610942692132232L;

    private String channelId;

    private Date start;

    private Date end;
}
