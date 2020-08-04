package net.zacard.xc.miniprogram.biz.util;

import lombok.extern.slf4j.Slf4j;
import net.zacard.xc.common.biz.infra.web.SpringContextHandle;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工具类
 *
 * @author guoqw
 * @since 2020-08-02 17:01
 */
@Slf4j
public class DLockUtil {

    private static StringRedisTemplate stringRedisTemplate = SpringContextHandle.getBean(StringRedisTemplate.class);

    public static void lockWithTimeout(Runnable runnable, long timeoutMilliseconds, String key) {
        if (!stringRedisTemplate.opsForValue().setIfAbsent(key, "1")) {
            return;
        }
        stringRedisTemplate.expire(key, timeoutMilliseconds, TimeUnit.MILLISECONDS);
        try {
            runnable.run();
        } finally {
            stringRedisTemplate.delete(key);
        }
    }

    public static void lockWithTimeoutAndCatch(Runnable runnable, long timeoutMilliseconds, String key) {
        if (!stringRedisTemplate.opsForValue().setIfAbsent(key, "1")) {
            return;
        }
        stringRedisTemplate.expire(key, timeoutMilliseconds, TimeUnit.MILLISECONDS);
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("分布式锁任务执行报错:" + e.getMessage(), e);
        } finally {
            stringRedisTemplate.delete(key);
        }
    }
}

