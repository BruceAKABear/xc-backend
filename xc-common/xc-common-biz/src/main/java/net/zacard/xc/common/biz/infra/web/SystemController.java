package net.zacard.xc.common.biz.infra.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 系统级controller，提供健康检查和版本检查接口
 * 不主动注册为@Controller,由使用方主动注册或者在web-boot-starter中配置开启
 *
 * @author guoqw
 * @since 2020-06-21 13:37
 */
@ResponseBody
@RequestMapping(value = "/api/system")
public class SystemController {

    @Value("${info.version:1.0.0}")
    private String version;

    /**
     * 简单健康检查
     */
    @GetMapping(value = "/health")
    public String healthCheck() {
        return "ok";
    }

    /**
     * 获取版本信息
     */
    @GetMapping(value = "/version")
    public String version() {
        return version;
    }
}
