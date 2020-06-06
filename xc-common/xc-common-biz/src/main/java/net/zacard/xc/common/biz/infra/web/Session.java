package net.zacard.xc.common.biz.infra.web;

import net.zacard.xc.common.biz.entity.UserAccessLog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guoqw
 * @since 2020-06-06 09:05
 */
public class Session {

    private static final Map<String, Object> HOLDER = new ConcurrentHashMap<>();

    public static void create(String userToken, UserAccessLog userAccessLog) {
        HOLDER.put(userToken, userAccessLog);
    }

    public static UserAccessLog user(String userToken) {
        return (UserAccessLog) HOLDER.get(userToken);
    }

    public static void clean(String userToken) {
        HOLDER.remove(userToken);
    }
}
