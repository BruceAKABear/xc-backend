package net.zacard.xc.common.biz.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author guoqw
 * @since 2020-06-11 16:17
 */
public class ObjectUtil {

    public static Map<String, String> objectToMapNonNull(Object obj) {
        if (obj == null) {
            return null;
        }
        //获取关联的所有类，本类以及所有父类
        Class oo = obj.getClass();
        List<Class> clazzs = new ArrayList<>();
        while (oo != null && oo != Object.class) {
            clazzs.add(oo);
            oo = oo.getSuperclass();
        }
        Map<String, String> map = new HashMap<>(clazzs.size() * 8);
        try {
            for (Class clazz : clazzs) {
                Field[] declaredFields = clazz.getDeclaredFields();
                for (Field field : declaredFields) {
                    int mod = field.getModifiers();
                    //过滤 static 和 final 类型
                    if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                        continue;
                    }
                    field.setAccessible(true);
                    Object fieldValue = field.get(obj);
                    if (fieldValue == null) {
                        continue;
                    }
                    map.put(field.getName(), fieldValue.toString());
                }
            }
        } catch (IllegalAccessException e) {
            // ignore
        }
        return map;
    }

    public static Map<String, Object> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        //获取关联的所有类，本类以及所有父类
        Class oo = obj.getClass();
        List<Class> clazzs = new ArrayList<>();
        while (oo != null && oo != Object.class) {
            clazzs.add(oo);
            oo = oo.getSuperclass();
        }
        Map<String, Object> map = new HashMap<>(clazzs.size() * 8);
        try {
            for (Class clazz : clazzs) {
                Field[] declaredFields = clazz.getDeclaredFields();
                for (Field field : declaredFields) {
                    int mod = field.getModifiers();
                    //过滤 static 和 final 类型
                    if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                        continue;
                    }
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(obj));
                }
            }
        } catch (IllegalAccessException e) {
            // ignore
        }
        return map;
    }
}
