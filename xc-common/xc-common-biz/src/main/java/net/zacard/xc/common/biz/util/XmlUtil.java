package net.zacard.xc.common.biz.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author guoqw
 * @since 2020-06-13 11:02
 */
@Slf4j
public class XmlUtil {

    private static final XmlMapper XML_MAPPER = new XmlMapper();

    static {
        XML_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String toXml(Object target) {
        try {
            return XML_MAPPER.writeValueAsString(target);
        } catch (Exception e) {
            log.error("对象转为xml报错", e);
            throw ExceptionUtil.unchecked(e);
        }
    }

    public static <T> T toObj(String xml, Class<T> clazz) {
        try {
            return XML_MAPPER.readValue(xml, clazz);
        } catch (Exception e) {
            log.error("xml转对象报错", e);
            throw ExceptionUtil.unchecked(e);
        }
    }
}
