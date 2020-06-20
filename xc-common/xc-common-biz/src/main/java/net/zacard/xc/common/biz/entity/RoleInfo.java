package net.zacard.xc.common.biz.entity;

import lombok.Data;
import net.zacard.xc.common.biz.infra.mongo.AuditDocument;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏角色信息
 *
 * @author guoqw
 * @since 2020-06-13 16:26
 */
@Data
@Document(collection = "role_info")
public class RoleInfo extends AuditDocument {

    private static final long serialVersionUID = 3598301856581390421L;

    private String userId;

    private String openid;

    private String channelId;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String name;

    /**
     * 区服名
     */
    private String area;

    /**
     * 角色等级
     */
    private String level;

    /**
     * 角色虚拟货币数量
     */
    private Integer money;

    /**
     * 用来做唯一区分：openid+channelId+area+name 来唯一确定一个角色，用英文逗号分隔
     */
    @Indexed(unique = true)
    private String token;

    public void buildToken() {
        List<String> tokenList = new ArrayList<>();
        tokenList.add(this.getOpenid());
        tokenList.add(this.getChannelId());
        tokenList.add(this.getArea());
        tokenList.add(this.getName());
        this.setToken(String.join(",", tokenList));
    }
}
