package net.zacard.xc.common.api.entity;

import java.io.Serializable;

/**
 * @author guoqw
 * @since 2020-06-01 16:06
 */
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1616716768355142282L;

    public static final Response EMPTY = new Response();

    public static final String HTTP_SUCCESS_CODE = "200";

    public static final String HTTP_FAIL_CODE = "500";

    private static final String SUCCESS_MSG = "OK";

    /**
     * 配置中的success code,优先于SUCCESS_CODE
     * 建议在容器启动的时候配置
     */
    public static volatile String successCodeFromProp;

    private static final String FAULT_MSG = "服务器内部错误";

    private String code;

    private String message;

    private T data;

    public static <T> Response<T> success() {
        return success(null);
    }

    public static <T> Response<T> success(T result) {
        return new Response<>(successCodeFromProp == null ? HTTP_SUCCESS_CODE : successCodeFromProp, SUCCESS_MSG,
                result);
    }

    public static <T> Response<T> fail() {
        return fail(FAULT_MSG);
    }

    public static <T> Response<T> fail(String errorMessage) {
        return new Response<>(HTTP_FAIL_CODE, errorMessage);
    }

    @SuppressWarnings("unchecked")
    public static <T> Response<T> emptyResponse() {
        return (Response<T>) EMPTY;
    }

    private Response() {
    }

    public Response(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getResult() {
        return data;
    }
}
