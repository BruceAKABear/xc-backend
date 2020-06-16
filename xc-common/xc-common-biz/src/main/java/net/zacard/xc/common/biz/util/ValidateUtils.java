package net.zacard.xc.common.biz.util;

import org.apache.commons.collections.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 参数校验工具类
 *
 * @author guoqw
 * @since 2018-11-12 11:29
 */
public class ValidateUtils {

    private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 校验对象的参数
     */
    public static String validateParamsProperty(Object param) {
        return validateParamsProperty(param, Default.class);
    }

    /**
     * 分组校验对象的参数
     */
    public static String validateParamsProperty(Object param, Class<?> group) {
        Set<Class<?>> groups = new HashSet<>(2);
        groups.add(Default.class);
        groups.add(group);
        return validateParamsProperty(param, new ArrayList<>(groups));
    }

    /**
     * 分组校验对象的参数
     */
    public static String validateParamsProperty(Object param, List<Class<?>> groups) {
        Set<ConstraintViolation<Object>> violations = validator.validate(param, groups.toArray(new Class[0]));
        if (CollectionUtils.isNotEmpty(violations)) {
            return violations.iterator().next().getMessage();
        }
        return null;
    }
}
