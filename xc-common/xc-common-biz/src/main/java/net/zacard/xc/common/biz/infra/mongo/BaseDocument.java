package net.zacard.xc.common.biz.infra.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * @author guoqw
 * @since 2020-05-05 15:26
 */
@Data
public class BaseDocument implements Serializable {

    private static final long serialVersionUID = 6742922586985804199L;

    @Id
    private String id;

    private Date createTime;

    private Date updateTime;

    /**
     * 是否删除
     */
    private Boolean deleted;
}
