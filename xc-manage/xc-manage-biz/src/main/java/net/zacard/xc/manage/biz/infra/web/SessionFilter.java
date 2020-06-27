package net.zacard.xc.manage.biz.infra.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 会话拦截器
 *
 * @author guoqw
 * @since 2020-06-21 14:03
 */
@Slf4j
public class SessionFilter implements Filter {

    private static final String USER_TOKEN_HEADER_NAME = "USER-TOKEN";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 获取header中的信息
        String userToken = httpRequest.getHeader(USER_TOKEN_HEADER_NAME);
        if (StringUtils.isBlank(userToken)) {
            chain.doFilter(request, response);
            return;
        }
        // TODO

    }

    @Override
    public void destroy() {

    }
}
