package net.zacard.xc.common.biz.infra.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * web项目的一些公共通用自动装配
 *
 * @author guoqw
 * @since 2018-08-13 10:38
 */
@Configuration
public class WebAutoConfig {

    @ConditionalOnMissingBean
    @Bean
    public SpringContextHandle springContextHandle() {
        return new SpringContextHandle();
    }

    /**
     * 目前没有特殊需求使用默认配置
     */
    @ConditionalOnMissingBean
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

}
