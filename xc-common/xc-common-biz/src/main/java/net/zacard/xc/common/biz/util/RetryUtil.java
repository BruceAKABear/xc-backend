package net.zacard.xc.common.biz.util;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author guoqw
 * @since 2020-06-27 16:26
 */
@Slf4j
public class RetryUtil {

    private static <T> Retryer<T> defaultRetryer(int retryTimes, long initTime, long incrementTime) {
        return RetryerBuilder.<T>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .retryIfRuntimeException()
                .withWaitStrategy(
                        WaitStrategies.incrementingWait(initTime, TimeUnit.SECONDS, incrementTime, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(retryTimes))
                .build();
    }

    public static void retry(Runnable runnable) {
        Callable<Boolean> callable = r2c(runnable);
        retry(callable);
    }

    public static <T> T retry(Callable<T> callable) {
        return retry(callable, defaultRetryer(3, 1, 1));
    }

    public static <T> T retry(Callable<T> callable, Retryer<T> retry) {
        try {
            return retry.call(callable);
        } catch (ExecutionException | RetryException e) {
            log.error("重试执行出错.", e);
            throw ExceptionUtil.unchecked(e);
        }
    }

    private static Callable<Boolean> r2c(Runnable runnable) {
        return () -> {
            runnable.run();
            return true;
        };
    }
}
