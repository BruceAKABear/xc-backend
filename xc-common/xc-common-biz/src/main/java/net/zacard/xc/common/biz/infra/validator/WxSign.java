package net.zacard.xc.common.biz.infra.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author guoqw
 * @since 2020-06-11 14:12
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {WxSignValidator.class})
public @interface WxSign {

    String message() default "签名校验失败";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
