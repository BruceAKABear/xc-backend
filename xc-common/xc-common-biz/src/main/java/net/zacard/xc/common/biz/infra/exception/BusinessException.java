package net.zacard.xc.common.biz.infra.exception;

/**
 * 统一的业务异常
 *
 * @author guoqw
 * @since 2020-05-26 14:20
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 6917623608519749603L;

    public static final String DEFAULT_CODE = "XC-ERROR-0001";

    private String code;

    private String message;

    public BusinessException(String message) {
        this(DEFAULT_CODE, message, new Throwable());
    }

    public BusinessException(Throwable throwable) {
        this(DEFAULT_CODE, "", throwable);
    }

    public BusinessException(String message, Throwable throwable) {
        this(DEFAULT_CODE, message, throwable);
    }

    public BusinessException(String code, String message) {
        this(code, message, new Throwable());
    }

    public BusinessException(String code, String message, Throwable cause) {
        super("[" + code + "] - " + message, cause);
        this.code = code;
        this.message = message;
    }

    public static BusinessException withMessage(String message) {
        return new BusinessException(message);
    }

    public static BusinessException withMessage(String message, Throwable cause) {
        return new BusinessException(DEFAULT_CODE, message, cause);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return "[" + code + "] - " + message;
    }

    public String getMessageWithoutCode() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
