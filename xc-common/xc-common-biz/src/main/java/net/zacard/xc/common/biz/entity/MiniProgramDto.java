package net.zacard.xc.common.biz.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-07-11 12:32
 */
@Data
public class MiniProgramDto implements Serializable {

    private static final long serialVersionUID = 132326681988133620L;

    /**
     * 小程序名称
     */
    @NotBlank(message = "name不能为空")
    private String name;

    /**
     * 小程序id
     */
    @NotBlank(message = "appid不能为空")
    private String appId;

    /**
     * 小程序版本
     */
    private String version;

    /**
     * 小程序展示的类型：game-游戏|info-资讯
     * 默认展示info资讯
     */
    private String showType;

    /**
     * 小程序展示的咨询
     */
    private Info info;

    /**
     * 小程序承接的展示的渠道
     */
    private Channel channel;

    /**
     * 其他配置
     */
//    private MiniProgramExtraConfig extraConfig;
}
