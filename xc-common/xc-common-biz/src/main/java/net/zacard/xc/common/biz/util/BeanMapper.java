package net.zacard.xc.common.biz.util;

import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对象属性复制
 * 使用Dozer操作，性能远超Apache Common BeanUtils
 *
 * @author guoqw
 * @since 2018-06-18 10:35
 */
public class BeanMapper {

    /**
     * 可以设置dozerBeanMapping.xml配置文件来配置字段映射关系
     * 会自动加载dozerBeanMapping.xml这个文件
     * 自动忽略null值
     */
    private static Mapper mapper = DozerBeanMapperBuilder.create()
            .withCustomFieldMapper(
                    (source, destination, sourceFieldValue, classMap, fieldMapping) -> sourceFieldValue == null)
            .build();

    public static <S, T> T map(S source, Class<T> targetClass) {
        return mapper.map(source, targetClass);
    }

    /**
     * 运行时映射，直接支持泛型list，数组等
     */
    public static void map(Object source, Object target) {
        mapper.map(source, target);
    }

    public static <S, T> List<T> map(List<S> sourceList, Class<T> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        return sourceList.stream()
                .map(source -> map(source, targetClass))
                .collect(Collectors.toList());
    }

}
