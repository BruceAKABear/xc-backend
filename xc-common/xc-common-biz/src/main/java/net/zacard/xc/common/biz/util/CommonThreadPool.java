package net.zacard.xc.common.biz.util;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.zacard.xc.common.biz.infra.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.zacard.xc.common.biz.util.CommonThreadPool.Param.runnableTasks;


/**
 * 通用线程池
 * <p>
 * 彻底用CompletableFuture重构
 * <p>
 * 优化内容：
 * 1.自动统计信息：总任务的耗时
 * 2.线程名称自动添加任务线索帮助dump出线程堆栈时候的分析(任务结束后线程名称将会重置为默认的名称)
 * 3.自动同步任务、回调任务等(可选)
 * 4.线程池监控报警(用自有的线程池将失去此功能)
 * 5.默认线程池定制，通过java参数指定
 * 6.任务自动分组，以免单次过多的任务涌入压垮线程池
 * 7.支持java spi的方式对线程池内执行的线程做功能增强
 * 8. ...
 *
 * @author guoqw
 * @since 2020-06-09 20:53
 */
public class CommonThreadPool {

    private static final Logger log = LoggerFactory.getLogger(CommonThreadPool.class);

    /**
     * 机器核数
     */
    private static final int THREAD_NUMBER = Runtime.getRuntime().availableProcessors();

    /**
     * 默认的任务线程线索名称
     */
    private static final String DEFAULT_THREAD_NAME_CLUE = "DEFAULT-CLUE";

    /**
     * 默认的线程名称前缀
     */
    private static final String DEFAULT_THREAD_NAME_PREFIX = "yxtools-commonPool-worker-";

    /**
     * 默认的线程池
     */
    private static final ExecutorService THREAD_POOL;

    /**
     * 工作队列的容量
     */
    private static final int QUEUE_CAPACITY = SystemPropertyUtil.getInt("yxTools.commonPool.queueSize", 512);

    /**
     * 批量任务自动分组的最大任务数量
     */
    private static final int TASK_PARTITION_NUM = 20;

    /**
     * 当单次提交到线程池的任务数超过队列容量的一半时，将强制给这批任务分组，以免压垮线程池
     */
    private static final int FORCE_PARTITION_TASK_SIZE = QUEUE_CAPACITY / 2;

    /**
     * 队列任务数量报警阈值
     */
    private static final double ALERT_QUEUE_SIZE = SystemPropertyUtil.getDouble("yxTools.commonPool.alertQueueSizePer",
            0.9);

    /**
     * 内部线程池的工作队列
     */
    private static final LinkedBlockingQueue<Runnable> WORK_QUEUE;

    // 初始化队列和线程池
    static {
        WORK_QUEUE = initWorkQueue();
        THREAD_POOL = initThreadPool();
    }

    /**
     * 初始化线程池的工作队列
     */
    private static LinkedBlockingQueue<Runnable> initWorkQueue() {
        int queueSize = QUEUE_CAPACITY;
        // 无界队列
        if (queueSize <= 0) {
            return new LinkedBlockingQueue<>();
        }
        // 有界队列
        return new LinkedBlockingQueue<>(queueSize);
    }

    /**
     * 初始化线程池
     */
    private static ExecutorService initThreadPool() {
        int coreSize = SystemPropertyUtil.getInt("yxTools.commonPool.coreSize", (8 + THREAD_NUMBER) * 2);
        int maxPoolSize = SystemPropertyUtil.getInt("yxTools.commonPool.maxSize", (16 + THREAD_NUMBER) * 2);
        long keepAliveTime = SystemPropertyUtil.getLong("yxTools.commonPool.keepAliveTime", 0L);
        return new MonitorThreadPoolExecutor(
                coreSize, maxPoolSize,
                keepAliveTime, TimeUnit.MILLISECONDS,
                WORK_QUEUE,
                new ThreadFactoryBuilder()
                        .setNameFormat(DEFAULT_THREAD_NAME_PREFIX + "%d-" + DEFAULT_THREAD_NAME_CLUE)
                        .build());
    }

    /**
     * 返回默认的线程池
     */
    private static ExecutorService defaultPool() {
        return THREAD_POOL;
    }

    /**
     * 提交单个任务
     *
     * @param taskClue 任务线索
     * @param task     任务
     */
    public static void submit(String taskClue, Runnable task) {
        submitAll(Collections.singletonList(task), taskClue);
    }

    /**
     * 批量提交异步任务
     *
     * @param tasks    任务集合
     * @param taskClue 任务线索
     */
    public static void submitAll(List<Runnable> tasks, String taskClue) {
        submitAll(taskClue, tasks, null, defaultPool());
    }

    /**
     * 批量提交异步任务,异步callback
     * 所有任务都结束的时候异步执行callback,期间callback不占用线程资源,整个过程不阻塞
     *
     * @param tasks        任务集合
     * @param asynCallback 回调方法
     * @deprecated 没有携带任务线索, 请使用带线索的方法
     */
    @Deprecated
    public static void submitAll(List<Runnable> tasks, AsynCallback asynCallback) {
        submitAll(tasks, asynCallback, THREAD_POOL);
    }

    /**
     * 批量提交异步任务,异步callback
     * 所有任务都结束的时候异步执行callback,期间callback不占用线程资源,整个过程不阻塞
     *
     * @param taskClue     任务线索
     * @param tasks        任务集合
     * @param asynCallback 回调方法
     */
    public static void submitAll(String taskClue, List<Runnable> tasks, AsynCallback asynCallback) {
        submitAll(taskClue, tasks, asynCallback, THREAD_POOL);
    }

    /**
     * 批量提交异步任务,并输出统计信息,例如任务耗时
     *
     * @param tasks    任务集合
     * @param taskClue 任务线索,在异步任务出错或者完成时，将携带任务线索信息以方便排查问题
     * @deprecated 现在所有方法都会输出统计信息
     */
    @Deprecated
    public static void submitAllWithStat(List<Runnable> tasks, String taskClue) {
        submitAllWithStat(tasks, taskClue, THREAD_POOL);
    }

    /**
     * 批量提交异步任务,并输出统计信息,例如任务耗时
     *
     * @param tasks    任务集合
     * @param taskClue 任务线索,在异步任务出错或者完成时，将携带任务线索信息以方便排查问题
     * @param pool     自定义的线程池
     * @deprecated 现在所有方法都会输出统计信息
     */
    @Deprecated
    public static void submitAllWithStat(List<Runnable> tasks, String taskClue, ExecutorService pool) {
        execute(runnableTasks(taskClue, tasks)
                .pool(pool)
                .build());
    }

    /**
     * 批量提交异步任务,异步callback
     * 所有任务都结束的时候异步执行callback,期间callback不占用线程资源,整个过程不阻塞
     *
     * @param tasks        任务集合
     * @param asynCallback 回调方法
     * @param pool         自定义的线程池
     * @deprecated 没有携带任务线索, 请使用带线索的方法
     */
    @Deprecated
    public static void submitAll(List<Runnable> tasks, AsynCallback asynCallback, ExecutorService pool) {
        submitAll("", tasks, asynCallback, pool);
    }

    /**
     * 批量提交异步任务,异步callback
     * 所有任务都结束的时候异步执行callback,期间callback不占用线程资源,整个过程不阻塞
     *
     * @param taskClue     任务线索
     * @param tasks        任务集合
     * @param asynCallback 回调方法
     * @param pool         自定义的线程池
     */
    public static void submitAll(String taskClue, List<Runnable> tasks, AsynCallback asynCallback, ExecutorService pool) {
        execute(runnableTasks(taskClue, tasks)
                .pool(pool)
                .asynCallback(asynCallback)
                .build());
    }

    /**
     * 并发执行任务，且等待所有任务执行完
     *
     * @param tasks 批量任务
     * @deprecated 请使用syncAllRunnableTask(List < Runnable > tasks, String taskClue)方法，传递这批任务的线索信息
     */
    @Deprecated
    public static void syncAllRunnableTask(List<Runnable> tasks) {
        syncAllRunnableTask(tasks, THREAD_POOL, null);
    }

    /**
     * 并发执行任务，且等待所有任务执行完
     *
     * @param tasks    批量任务
     * @param taskClue 任务线索,在异步任务出错或者完成时，将携带任务线索信息以方便排查问题
     */
    public static void syncAllRunnableTask(List<Runnable> tasks, String taskClue) {
        syncAllRunnableTask(tasks, THREAD_POOL, taskClue);
    }

    /**
     * 并发执行任务，且等待所有任务执行完
     *
     * @param tasks    批量任务
     * @param pool     自定义线程池
     * @param taskClue 任务线索,在异步任务出错或者完成时，将携带任务线索信息以方便排查问题
     */
    public static void syncAllRunnableTask(List<Runnable> tasks, ExecutorService pool, String taskClue) {
        execute(runnableTasks(taskClue, tasks)
                .waitForComplete(true)
                .pool(pool)
                .build());
    }

    /**
     * 并发执行任务，且等待所有任务执行完,并按照顺序返回结果
     * Callable等有返回值的任务不会自动分组！
     *
     * @param tasks 任务集合
     * @deprecated 没有携带任务线索信息, 请使用syncAllCallableTask(String taskClue, List < Callable < T > > tasks)
     */
    @Deprecated
    public static <T> List<T> syncAllCallableTask(List<Callable<T>> tasks) {
        return syncAllCallableTask("", tasks, null);
    }

    /**
     * 并发执行任务，且等待所有任务执行完,并按照顺序返回结果
     * Callable等有返回值的任务不会自动分组！
     *
     * @param tasks 任务集合
     */
    public static <T> List<T> syncAllCallableTask(String taskClue, List<Callable<T>> tasks) {
        return syncAllCallableTask(taskClue, tasks, null);
    }

    /**
     * 并发执行任务，且等待所有任务执行完,并按照顺序返回结果
     * Callable等有返回值的任务不会自动分组！
     *
     * @param taskClue 任务线索
     * @param tasks    任务集合
     * @param timeout  超时时间（毫秒）
     */
    public static <T> List<T> syncAllCallableTask(String taskClue, List<Callable<T>> tasks, Long timeout) {
        List<? extends Future<T>> futures = syncAllCallableTaskWithFuture(taskClue, tasks);
        List<T> result = new ArrayList<>(tasks.size());
        try {
            for (Future<T> future : futures) {
                result.add(timeout == null ? future.get() : future.get(timeout, TimeUnit.MILLISECONDS));
            }
        } catch (Exception e) {
            throw new BusinessException("common-threadPool-001", "任务(" + taskClue + ")执行异常", e);
        }
        return result;
    }

    public static <T> List<? extends Future<T>> submit(String taskClue, List<Callable<T>> tasks) {
        return submit(taskClue, tasks, true);
    }

    public static <T> List<? extends Future<T>> submit(String taskClue, List<Callable<T>> tasks, boolean waitForComplete) {
        List<CompletableFuture<?>> futures = submit(Param.callableTasks(taskClue, tasks)
                .waitForComplete(waitForComplete)
                .build());
        return futureCastHelper(futures);
    }

    /**
     * 并发执行任务，且等待所有任务执行完,并返回Future
     * Callable等有返回值的任务不会自动分组！
     *
     * @param tasks 任务集合
     */
    public static <T> List<? extends Future<T>> syncAllCallableTaskWithFuture(String taskClue, List<Callable<T>> tasks) {
        List<CompletableFuture<?>> futures = submit(Param.callableTasks(taskClue, tasks)
                .waitForComplete(true)
                .build());
        return futureCastHelper(futures);
    }

    /**
     * 这里是一个bad case，因为泛型通配符CompletableFuture<?>无法很好的转为CompletableFuture<T>,只能求助于不安全的强转
     */
    @SuppressWarnings("unchecked")
    private static <T> List<? extends Future<T>> futureCastHelper(List<CompletableFuture<?>> futures) {
        return futures.stream()
                .map(future -> (CompletableFuture<T>) future)
                .collect(Collectors.toCollection(() -> new ArrayList<>(futures.size())));
    }

    public static Runnable wrap2Count(AtomicInteger count, Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                log.error("捕获到并发任务执行异常.", e);
                throw ExceptionUtil.unchecked(e);
            } finally {
                count.decrementAndGet();
            }
        };
    }

    /**
     * 将异步任务try-catch住，以免异常丢失
     *
     * @param taskClue 任务线索描述
     * @param tasks    任务集合
     */
    public static Runnable wrapWithCatch(String taskClue, List<Runnable> tasks) {
        for (ThreadPoolRunnableProcessor threadPoolRunnableProcessor : ServiceLoader.load(
                ThreadPoolRunnableProcessor.class)) {
            tasks = tasks.stream()
                    .map(threadPoolRunnableProcessor::process)
                    .collect(Collectors.toList());
        }
        List<Runnable> finalTasks = tasks;
        return () -> {
            String rawThreadName = Thread.currentThread().getName();
            try {
                // 将任务线索设置到线程名称中以方便在dump出线程堆栈排查问题是提供线索
                Thread.currentThread().setName(replaceThreadName(rawThreadName, taskClue));
                finalTasks.forEach(Runnable::run);
            } catch (Exception e) {
                log.error("捕获到并发任务(" + taskClue + ")异常.", e);
                throw ExceptionUtil.unchecked(e);
            } finally {
                // 执行结束后将线程名称重置为原先的名称
                Thread.currentThread().setName(rawThreadName);
            }
        };
    }

    /**
     * 将任务线索设置到线程名称中以方便在dump出线程堆栈排查问题是提供线索
     */
    private static String replaceThreadName(String threadName, String taskClue) {
        String newThreadName = threadName.replace(DEFAULT_THREAD_NAME_CLUE, taskClue);
        // 这里说明客户端给的任务线索和默认的线程名称前缀有冲突(比如taskClue=="-")，就不替换了使用原有的名称
        if (!newThreadName.startsWith(DEFAULT_THREAD_NAME_PREFIX)) {
            return threadName;
        }
        return newThreadName;
    }

    /**
     * 将异步任务try-catch住，以免异常丢失
     */
    private static <T> Supplier<T> wrapCallableWithCatch(String taskClue, Callable<T> task) {
        for (ThreadPoolCallableProcessor threadPoolCallableProcessor : ServiceLoader.load(
                ThreadPoolCallableProcessor.class)) {
            task = threadPoolCallableProcessor.process(task);
        }
        Callable<T> finalTask = task;
        return () -> {
            try {
                return finalTask.call();
            } catch (Exception e) {
                log.error("捕获到并发任务(" + taskClue + ")异常.", e);
                throw ExceptionUtil.unchecked(e);
            }
        };
    }

    /**
     * 异步回调函数用catch包装，并且输出统计信息
     */
    private static <T> BiConsumer<? super T, ? super Throwable> wrapCallbackWithStat(boolean force,
                                                                                     String taskClue,
                                                                                     long start,
                                                                                     AsynCallback asynCallback,
                                                                                     List<? extends Future<?>> futures) {
        return (r, t) -> {
            if (asynCallback != null) {
                try {
                    asynCallback.call(futures);
                } catch (Exception e) {
                    log.error("并发任务(" + taskClue + ")的回调函数执行出错", e);
                    throw ExceptionUtil.unchecked(e);
                } finally {
                    Stat.logCostTimeNow(force, taskClue, start);
                }
            } else {
                Stat.logCostTimeNow(force, taskClue, start);
            }
        };
    }

    /**
     * 执行runnable任务
     */
    public static List<CompletableFuture<Void>> execute(Param param) {
        long start = System.currentTimeMillis();
        List<Runnable> tasks = param.getTasks();
        if (tasks == null || tasks.isEmpty()) {
            return Collections.emptyList();
        }
        String taskClue = param.getTaskClue();
        // 任务分组
        List<List<Runnable>> pTasks = taskPartition(tasks, param.group ? 0 : 1);
        // 执行的线程池
        final ExecutorService pool = param.getOrDefaultPool(defaultPool());
        // 异步任务执行
        List<CompletableFuture<Void>> futures = pTasks.stream()
                .map(rTasks -> CompletableFuture.runAsync(wrapWithCatch(taskClue, rTasks), pool))
                .collect(Collectors.toList());
        // 等待所有异步任务结束后执行回调函数
        AsynCallback asynCallback = param.getAsynCallback();
        CompletableFuture<Void> allFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        CompletableFuture<Void> completableFuture = allFuture.whenCompleteAsync(
                wrapCallbackWithStat(param.isForceLogStat(), taskClue, start, asynCallback, futures),
                pool);
        // 执行后的处理
        afterExecution(param, completableFuture, allFuture);
        return futures;
    }

    /**
     * 执行callable任务,callable任务是不分组的
     */
    public static List<CompletableFuture<?>> submit(Param param) {
        long start = System.currentTimeMillis();
        List<? extends Callable<?>> callables = param.getCallables();
        if (callables == null || callables.isEmpty()) {
            return Collections.emptyList();
        }
        int size = callables.size();
        if (size >= FORCE_PARTITION_TASK_SIZE) {
            log.warn("[yxTools-commonPool-alert]单次提交到线程池的任务数({})过多,可能压垮线程池,建议对任务分组.", size);
        }
        String taskClue = param.getTaskClue();
        // 执行的线程池
        final ExecutorService pool = param.getOrDefaultPool(defaultPool());
        // 异步执行任务
        List<CompletableFuture<?>> futures = callables.stream()
                .map(tCallable -> CompletableFuture.supplyAsync(wrapCallableWithCatch(taskClue, tCallable), pool))
                .collect(Collectors.toList());
        // 等待所有异步任务结束后执行回调函数
        AsynCallback asynCallback = param.getAsynCallback();
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        CompletableFuture<Void> completableFuture = allOf.whenCompleteAsync(
                wrapCallbackWithStat(param.isForceLogStat(), taskClue, start, asynCallback, futures),
                pool);
        // 执行后的处理
        afterExecution(param, completableFuture, allOf);
        return futures;
    }

    /**
     * 执行后的处理
     */
    private static void afterExecution(Param param, CompletableFuture<Void> completableFuture, CompletableFuture<Void> allOf) {
        // 等待所有异步任务结束
        if (param.isWaitForComplete()) {
            try {
                completableFuture.join();
            } catch (CompletionException e) {
                // 异步任务出错的情况
                if (param.isThrowException2Main()) {
                    throw e;
                } else {
                    log.error("任务(" + param.getTaskClue() + ")其中的异步任务出错.", e);
                }
            }
        }
    }

    /**
     * 通用线程池自我保护机制
     * 给任务分组,每个分组的任务将顺序执行,避免同一时间大量任务涌入将线程池塞满
     * 默认任务分为10-20个组
     */
    private static List<List<Runnable>> taskPartition(List<Runnable> tasks) {
        return taskPartition(tasks, 0);
    }

    /**
     * 通用线程池自我保护机制
     * 给任务分组,每个分组的任务将顺序执行,避免同一时间大量任务涌入将线程池塞满
     *
     * @param tasks     任务集合
     * @param groupSize 每组的任务数量,groupSize<=0的时候将自动分组，分为10-20个组
     * @param <T>       Runnable|Callable
     */
    private static <T> List<List<T>> taskPartition(List<T> tasks, int groupSize) {
        // 当单次提交到线程池的任务数超过队列容量的一半时，将强制给这批任务分组，以免压垮线程池
        if (tasks.size() >= FORCE_PARTITION_TASK_SIZE) {
            groupSize = 0;
        }
        if (groupSize > 0) {
            return Lists.partition(tasks, groupSize);
        }
        // 自动分组,分为10-20个组
        // 计算出每个分组的任务数量
        int size = tasks.size();
        if (size <= TASK_PARTITION_NUM) {
            groupSize = 1;
        } else {
            groupSize = size / 10;
        }
        return Lists.partition(tasks, groupSize);
    }

    /**
     * 异步callback
     */
    public interface AsynCallback {

        /**
         * 回调方法
         *
         * @param futures 所有异步任务的future
         */
        void call(List<? extends Future<?>> futures);
    }

    /**
     * 带有监控功能的ThreadPoolExecutor
     * 默认队列容量超过阈值(默认90%)将报警
     */
    public static class MonitorThreadPoolExecutor extends ThreadPoolExecutor {

        public MonitorThreadPoolExecutor(int corePoolSize,
                                         int maximumPoolSize,
                                         long keepAliveTime,
                                         TimeUnit unit,
                                         BlockingQueue<Runnable> workQueue,
                                         ThreadFactory threadFactory) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            // 无界队列不需要监控
            if (QUEUE_CAPACITY <= 0 || QUEUE_CAPACITY == Integer.MAX_VALUE) {
                return;
            }
            try {
                BlockingQueue<Runnable> queue = getQueue();
                int i = queue.remainingCapacity();
                // 无界队列不需要监控
                if (i == Integer.MAX_VALUE) {
                    return;
                }
                int size = queue.size();
                double sizePer = (double) size / QUEUE_CAPACITY;
                if (sizePer > ALERT_QUEUE_SIZE) {
                    log.warn(
                            "[yxTools-commonPool-alert]当前线程池工作队列容量占比(" + sizePer + ")已经超过设置阈值(" + ALERT_QUEUE_SIZE + "),请注意处理.");
                }
            } catch (Exception e) {
                log.error("监控线程池队列容量报错", e);
            }
        }
    }

    /**
     * 运行相关统计
     */
    private static class Stat {

        /**
         * 默认耗时超过10s的任务将以info级别输出
         */
        private static final long DEFAULT_INFO_TIME_MILLISECONDS = 10 * 1000;

        private static long infoTimeMilliseconds = SystemPropertyUtil.getLong("yx_infoTimeMilliseconds",
                DEFAULT_INFO_TIME_MILLISECONDS);

        /**
         * 默认耗时超过30s的任务将以warn级别输出
         */
        private static final long DEFAULT_WARN_TIME_MILLISECONDS = 30 * 1000;

        private static long warnTimeMilliseconds = SystemPropertyUtil.getLong("yx_warnTimeMilliseconds",
                DEFAULT_WARN_TIME_MILLISECONDS);

        /**
         * 默认耗时超过15分钟的任务将以error级别输出
         */
        private static final long DEFAULT_ERROR_TIME_MILLISECONDS = 15 * 60 * 1000;

        private static long errorTimeMilliseconds = SystemPropertyUtil.getLong("yx_errorTimeMilliseconds",
                DEFAULT_ERROR_TIME_MILLISECONDS);

        /**
         * 输出到现在为止的耗时日志
         *
         * @param force     是否强制使用info输出
         * @param taskDesc  任务描述
         * @param startTime 任务开始执行的时间戳
         */
        private static void logCostTimeNow(boolean force, String taskDesc, long startTime) {
            long actualCostTimeMilliseconds = System.currentTimeMillis() - startTime;
            logCostTime(force, taskDesc, actualCostTimeMilliseconds);
        }

        /**
         * 输出耗时日志
         */
        private static void logCostTime(boolean force, String taskDesc, long actualCostTimeMilliseconds) {
            // 根据实际耗时输出
            if (actualCostTimeMilliseconds < infoTimeMilliseconds) {
                // 强制直接用info输出
                if (force) {
                    log.info("任务({})执行结束,耗时:{}ms", taskDesc, actualCostTimeMilliseconds);
                    return;
                }
                log.debug("任务({})执行结束,耗时:{}ms", taskDesc, actualCostTimeMilliseconds);
            } else if (actualCostTimeMilliseconds < warnTimeMilliseconds) {
                log.info("任务({})执行结束,耗时:{}ms", taskDesc, actualCostTimeMilliseconds);
            } else if (actualCostTimeMilliseconds < errorTimeMilliseconds) {
                log.warn("任务({})执行结束,耗时:{}ms", taskDesc, actualCostTimeMilliseconds);
            } else {
                log.error("任务({})执行结束,耗时:{}ms", taskDesc, actualCostTimeMilliseconds);
            }
        }
    }

    public static class Param {

        /**
         * 任务线索
         */
        private String taskClue;

        /**
         * 任务集合
         */
        private List<Runnable> tasks;

        /**
         * callable任务集合
         */
        private List<? extends Callable<?>> callables;

        /**
         * 是否自动任务分组
         */
        private boolean group = false;

        /**
         * 是否输出统计信息，例如任务的运行时间
         */
        private boolean stat = true;

        /**
         * 直接使用info级别输出任务的统计信息，例如耗时
         */
        private boolean forceLogStat = true;

        /**
         * 等待任务完成
         */
        private boolean waitForComplete = false;

        /**
         * 异步任务出错的时候是否抛出异常到主线程
         * ps:只在waitForComplete==true的时候生效
         */
        private boolean throwException2Main = true;

        /**
         * 自定义线程池
         */
        private ExecutorService pool;

        /**
         * 异步回调
         */
        private AsynCallback asynCallback;

        private Param() {
        }

        public ExecutorService getOrDefaultPool(ExecutorService defaultPool) {
            return pool == null ? defaultPool : pool;
        }

        private Param(ParamBuilder paramBuilder) {
            setTaskClue(paramBuilder.taskClue);
            setTasks(paramBuilder.tasks);
            setCallables(paramBuilder.callables);
            setGroup(paramBuilder.group);
            setStat(paramBuilder.stat);
            setWaitForComplete(paramBuilder.waitForComplete);
            setPool(paramBuilder.pool);
            setAsynCallback(paramBuilder.asynCallback);
            setForceLogStat(paramBuilder.forceLogStat);
            setThrowException2Main(paramBuilder.throwException2Main);
        }

        public static ParamBuilder runnableTasks(String taskClue, List<Runnable> tasks) {
            return ParamBuilder.runnableTasks(taskClue, tasks);
        }

        public static ParamBuilder callableTasks(String taskClue, List<? extends Callable<?>> tasks) {
            return ParamBuilder.callableTasks(taskClue, tasks);
        }

        public boolean isThrowException2Main() {
            return throwException2Main;
        }

        public void setThrowException2Main(boolean throwException2Main) {
            this.throwException2Main = throwException2Main;
        }

        public String getTaskClue() {
            return taskClue;
        }

        public void setTaskClue(String taskClue) {
            this.taskClue = taskClue;
        }

        public List<Runnable> getTasks() {
            return tasks;
        }

        public void setTasks(List<Runnable> tasks) {
            this.tasks = tasks;
        }

        public boolean isGroup() {
            return group;
        }

        public void setGroup(boolean group) {
            this.group = group;
        }

        public boolean isStat() {
            return stat;
        }

        public void setStat(boolean stat) {
            this.stat = stat;
        }

        public boolean isWaitForComplete() {
            return waitForComplete;
        }

        public void setWaitForComplete(boolean waitForComplete) {
            this.waitForComplete = waitForComplete;
        }

        public ExecutorService getPool() {
            return pool;
        }

        public void setPool(ExecutorService pool) {
            this.pool = pool;
        }

        public AsynCallback getAsynCallback() {
            return asynCallback;
        }

        public void setAsynCallback(AsynCallback asynCallback) {
            this.asynCallback = asynCallback;
        }

        public List<? extends Callable<?>> getCallables() {
            return callables;
        }

        public void setCallables(List<? extends Callable<?>> callables) {
            this.callables = callables;
        }

        public boolean isForceLogStat() {
            return forceLogStat;
        }

        public void setForceLogStat(boolean forceLogStat) {
            this.forceLogStat = forceLogStat;
        }

        public static final class ParamBuilder {
            private String taskClue;
            private List<Runnable> tasks;
            private List<? extends Callable<?>> callables;

            private boolean group;
            private boolean stat = true;
            private boolean waitForComplete;
            private boolean forceLogStat = true;
            private boolean throwException2Main = true;
            private ExecutorService pool;
            private AsynCallback asynCallback;

            private ParamBuilder() {
            }

            private ParamBuilder(String taskClue, List<Runnable> tasks) {
                this.taskClue = taskClue;
                this.tasks = tasks;
            }

            private static ParamBuilder runnableTasks(String taskClue, List<Runnable> tasks) {
                return new ParamBuilder()
                        .group(true)
                        .taskClue(taskClue)
                        .tasks(tasks);
            }

            private static ParamBuilder callableTasks(String taskClue, List<? extends Callable<?>> tasks) {
                return new ParamBuilder()
                        .taskClue(taskClue)
                        .callables(tasks);
            }

            public ParamBuilder throwException2Main(boolean val) {
                throwException2Main = val;
                return this;
            }

            public ParamBuilder taskClue(String val) {
                taskClue = val;
                return this;
            }

            public ParamBuilder tasks(List<Runnable> val) {
                tasks = val;
                return this;
            }

            public ParamBuilder callables(List<? extends Callable<?>> val) {
                callables = val;
                return this;
            }

            public ParamBuilder group(boolean val) {
                group = val;
                return this;
            }

            public ParamBuilder stat(boolean val) {
                stat = val;
                return this;
            }

            public ParamBuilder forceLogStat(boolean val) {
                forceLogStat = val;
                return this;
            }

            public ParamBuilder waitForComplete(boolean val) {
                waitForComplete = val;
                return this;
            }

            public ParamBuilder pool(ExecutorService val) {
                pool = val;
                return this;
            }

            public ParamBuilder asynCallback(AsynCallback val) {
                asynCallback = val;
                return this;
            }

            public Param build() {
                return new Param(this);
            }
        }
    }

    /**
     * 对Runnable做一些增强处理
     */
    public interface ThreadPoolRunnableProcessor {

        Runnable process(Runnable runnable);
    }

    /**
     * 对Callable做一些增强处理
     */
    public interface ThreadPoolCallableProcessor {

        <V> Callable<V> process(Callable<V> callable);
    }

}
