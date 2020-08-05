package net.zacard.xc.common.biz.infra.web;

import net.zacard.xc.common.biz.infra.filter.AccessLogFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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

    @ConditionalOnMissingBean
    @Bean
    public SystemController systemController() {
        return new SystemController();
    }

    @Bean
    @ConditionalOnProperty(prefix = "xc.web.accesslog", name = "enable", matchIfMissing = true)
    public FilterRegistrationBean accessLogFilterRegistration() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        //定义过滤器顺序
        filterRegistrationBean.setOrder(0);
        filterRegistrationBean.setFilter(new AccessLogFilter());
        filterRegistrationBean.setName("accessLogFilter");
        return filterRegistrationBean;
    }
}
