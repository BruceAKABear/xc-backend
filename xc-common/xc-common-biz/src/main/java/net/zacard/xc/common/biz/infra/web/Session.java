package net.zacard.xc.common.biz.infra.web;

import net.zacard.xc.common.biz.entity.UserAccessLog;
import net.zacard.xc.common.biz.infra.exception.BusinessException;
import net.zacard.xc.common.biz.repository.UserAccessLogRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO 类似jwt，能够解密出来基本信息
 *
 * @author guoqw
 * @since 2020-06-06 09:05
 */
public class Session {

    private static final ThreadLocal<UserAccessLog> T_HOLDER = new ThreadLocal<>();

    private static final Map<String, Object> HOLDER = new ConcurrentHashMap<>();

    private static UserAccessLogRepository userAccessLogRepository = SpringContextHandle.getBean(
            UserAccessLogRepository.class);

    public static void create(String userToken, UserAccessLog userAccessLog) {
        HOLDER.put(userToken, userAccessLog);
    }

    public static void update(String userToken, UserAccessLog userAccessLog) {
        HOLDER.put(userToken, userAccessLog);
    }

    public static UserAccessLog user(String userToken) {
        Object o = HOLDER.get(userToken);
        if (o == null) {
            // 从库里加载
            return userAccessLogRepository.findByUserToken(userToken);
        }
        return (UserAccessLog) o;
    }

    public static UserAccessLog checkedUser(String userToken) {
        UserAccessLog userAccessLog = user(userToken);
        if (userAccessLog == null) {
            throw BusinessException.withMessage("用户会话失效");
        }
        return userAccessLog;
    }

    public static void clean(String userToken) {
        HOLDER.remove(userToken);
    }

    /**
     * 获取当前会话用户信息
     */
    public static UserAccessLog current() {
        return T_HOLDER.get();
    }

    /**
     * 初始化当前用户信息
     */
    public static void initCurrent(String userToken) {

    }

}
