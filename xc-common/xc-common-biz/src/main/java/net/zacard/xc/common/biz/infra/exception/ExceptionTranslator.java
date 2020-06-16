package net.zacard.xc.common.biz.infra.exception;

import net.zacard.xc.common.api.entity.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * 全局异常处理，使异常显示更友好
 *
 * @author guoqw
 * @since 2020-05-16 14:41
 */
@ControllerAdvice
public class ExceptionTranslator {

    private final Logger logger = LoggerFactory.getLogger(ExceptionTranslator.class);

    /**
     * 业务异常，返回详细的信息
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Response processBusinessException(BusinessException e) {
        logger.error("catch exception:" + e.getMessage(), e);
        return new Response(e.getCode(), e.getMessageWithoutCode());
    }

    /**
     * 参数校验异常，返回具体信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Response processValidateException(MethodArgumentNotValidException e) {
        // 这里将异常转为BusinessException,这类异常无需报警,属于业务型异常
        logger.error("catch exception:" + e.getMessage(), new BusinessException(e));
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> "[" + error.getDefaultMessage() + "] ")
                .collect(Collectors.joining());
        return Response.fail(errorMessage);
    }

    /**
     * 参数绑定异常，返回具体信息
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Response processBindException(BindException e) {
        logger.error("catch exception:" + e.getMessage(), e);
        String errorMessage = e.getAllErrors()
                .stream()
                .map(error -> "[" + error.getDefaultMessage() + "]")
                .collect(Collectors.joining());
        return Response.fail(errorMessage);
    }

    /**
     * rpc调用抛出的参数校验异常
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public Response processRpcValidateException(ValidationException e) {
        logger.error("catch exception:" + e.getMessage(), e);
        if (e instanceof ConstraintViolationException) {
            String errorMessage = ((ConstraintViolationException) e).getConstraintViolations()
                    .stream()
                    .map(error -> "[" + error.getMessage() + "]")
                    .collect(Collectors.joining());
            return Response.fail(errorMessage);
        }
        return Response.fail();
    }

    /**
     * 非业务异常统一返回"服务器内部异常"
     */
    @ExceptionHandler
    @ResponseBody
    public void processException(HttpServletResponse response, Exception e) {
        logger.error("catch exception:" + e.getMessage(), e);
        try {
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误");
        } catch (IOException ignore) {
        }
    }
}
