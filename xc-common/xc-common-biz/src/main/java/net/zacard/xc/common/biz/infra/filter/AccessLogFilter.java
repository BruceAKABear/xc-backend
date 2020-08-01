package net.zacard.xc.common.biz.infra.filter;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * accessLog Filter
 *
 * @author guoqw
 * @since 2020-06-06 16:04
 */
public class AccessLogFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogFilter.class);

    private static final int MAX_LENGTH = 8000;

    private static final String METHOD_GET = "GET";

    private static final int SUCCESS_CODE = 200;

    private FilterConfig config;

    /**
     * 未知ip
     */
    private static final String UNKNOWN = "unknown";
    /**
     * 通过代理转发时（通常为Nginx），设置的真实客户端ip的信息头字段
     */
    private static final String FORWARD = "x-forwarded-for";
    /**
     * 通过代理转发时（通常为Apache），设置的真实客户端ip的信息头字段
     */
    private static final String PROXY = "Proxy-Client-IP";
    /**
     * 通过代理转发时（通常为Apache），设置的真实客户端ip的信息头字段
     */
    private static final String WL_PROXY = "WL-Proxy-Client-IP";

    /**
     * IP分隔符
     */
    private static final String IP_SEPARATOR = ",";

    /**
     * 内部忽略的url
     */
    private static final List<String> INNER_IGNORE_URLS = Arrays.asList("/system/health", "/system/version");

    /**
     * 内部忽略的静源
     */
    private static final List<String> INNER_STATIC_RESPURCE = Arrays.asList(".css", ".ico", "/img", "/icons", ".js",
            ".ttf", ".html");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        config = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();

        // 内部忽略url
        if (INNER_IGNORE_URLS.stream().anyMatch(uri::endsWith)) {
            chain.doFilter(request, response);
            return;
        }

        // 内部忽略的静态资源
        if (INNER_STATIC_RESPURCE.stream().anyMatch(uri::endsWith)) {
            chain.doFilter(request, response);
            return;
        }

        WrapperedRequest wrapperedRequest = new WrapperedRequest(httpRequest);
        AccessLog accessLog = new AccessLog();
        accessLog.setMethod(httpRequest.getMethod());
        accessLog.setClientIP(getRequestRealIp(httpRequest));
        accessLog.setUri(uri);
        String requestParam = JSON.toJSONString(httpRequest.getParameterMap());
        //非GET请求，获取请求body
        if (!METHOD_GET.equals(httpRequest.getMethod().toUpperCase())) {
            requestParam = getRequestBody(wrapperedRequest);
        }
        if (requestParam.length() > MAX_LENGTH) {
            requestParam = requestParam.substring(0, MAX_LENGTH) + "...";
        }
        accessLog.setParameters(requestParam);

        Long startTime = System.currentTimeMillis();
        accessLog.setRequestTime(longToDate(startTime));

        WrapperedResponse wrapResponse = new WrapperedResponse(httpResponse);

        try {
            chain.doFilter(wrapperedRequest, wrapResponse);
        } catch (Exception e) {
            accessLog.setException(e.getMessage());
            throw e;
        }

        Long endTime = System.currentTimeMillis();
        int responseStatus = httpResponse.getStatus();
        accessLog.setSuccess(responseStatus == SUCCESS_CODE);
        accessLog.setHttpStatus(String.valueOf(responseStatus));
        accessLog.setTime(endTime - startTime);
        byte[] data = wrapResponse.getResponseData();
        //响应格式为json的，记录响应内容
        if (httpResponse.getContentType() != null && httpResponse.getContentType().toLowerCase().contains("json")) {
            String responseStr = new String(data, StandardCharsets.UTF_8);
            if (responseStr.length() > MAX_LENGTH) {
                responseStr = responseStr.substring(0, MAX_LENGTH) + "...";
            }
            accessLog.setResponse(responseStr);
        }
        accessLog.setResponseTime(longToDate(endTime));
        LOGGER.info(JSON.toJSONString(accessLog));
        ServletOutputStream out = response.getOutputStream();
        out.write(data);
        out.flush();
    }

    @Override
    public void destroy() {
    }

    private static String longToDate(long lo) {
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sd.format(date);
    }

    private String getRequestBody(HttpServletRequest request) {
        try {
            BufferedReader br = request.getReader();
            return br.lines().collect(Collectors.joining());
        } catch (IOException ex) {
            LOGGER.error("获取servlet request body失败", ex);
        }
        return "";
    }

    /**
     * 获取请求的真实客户端ip：被多级反向代理或者跳转转发的请求
     */
    private String getRequestRealIp(ServletRequest servletRequest) {
        String ip = null;
        HttpServletRequest request = null;
        if (servletRequest instanceof HttpServletRequest) {
            request = (HttpServletRequest) servletRequest;
        }
        if (request != null) {
            ip = request.getHeader(FORWARD);
            if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
                ip = request.getHeader(PROXY);
            }
            if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
                ip = request.getHeader(WL_PROXY);
            }
        }
        if (StringUtils.isBlank(ip) || StringUtils.equalsIgnoreCase(UNKNOWN, ip)) {
            ip = servletRequest.getRemoteAddr();
        }
        //代理ip可能有多级，ip也会有多个，第一个不是“unknown”的ip为真实客户端ip
        if (StringUtils.isNotBlank(ip) && StringUtils.split(ip, IP_SEPARATOR).length > 0) {
            for (String s : StringUtils.split(ip, IP_SEPARATOR)) {
                if (!StringUtils.equalsIgnoreCase(UNKNOWN, s)) {
                    ip = s;
                    break;
                }
            }
        }
        return ip;
    }


}
