package com.wellsoft.pt.basicdata.iexport.suport;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.wellsoft.context.util.ApplicationContextHolder;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年11月08日   chenq	 Create
 * </pre>
 */
public class ExportTreeContextHolder {

    private static TransmittableThreadLocal<Set<String>> keys = new TransmittableThreadLocal<Set<String>>();

    private static TransmittableThreadLocal<Set<String>> excludeKeys = new TransmittableThreadLocal<Set<String>>();

    private static TransmittableThreadLocal<Boolean> exportDependency = new TransmittableThreadLocal<>();

    private static TransmittableThreadLocal<List<Callable>> callables = new TransmittableThreadLocal();


    // 使用线程安全的队列存储待执行任务
    private static final TransmittableThreadLocal<ConcurrentLinkedQueue<Callable<Void>>> taskQueue = new TransmittableThreadLocal();
    private static final TransmittableThreadLocal<AtomicInteger> activeTaskCount = new TransmittableThreadLocal();
    // 记录最后一次提交任务的时间（纳秒或毫秒）
    private static TransmittableThreadLocal<Long> lastTaskSubmitTime = new TransmittableThreadLocal();
    // 全局空闲超时阈值（例如：3000ms 内无新任务即认为结束）
    private static final long IDLE_TIMEOUT_MS = 3000;


    private static TransmittableThreadLocal<Boolean> enableThread = new TransmittableThreadLocal<>();

    private static Logger logger = LoggerFactory.getLogger(ExportTreeContextHolder.class);


    public static synchronized boolean add(String key) {
        if (keys.get() == null) {
            keys.set(Sets.newConcurrentHashSet());
        }
        return keys.get().add(key);
    }


    public static void waitForAllCallableDone(int timeout, TimeUnit timeUnit) {
        try {
            if (taskQueue.get() != null) {
                ListeningExecutorService executorService = (ListeningExecutorService) ApplicationContextHolder.getBean("sharedGuavaPool");
                long overallDeadline = System.currentTimeMillis() + timeUnit.toMillis(timeout);
                int batchIndex = 0;
                while (true) {
                    // 尝试拉取一个批次
                    int batchSize = 10;
                    List<Callable<Void>> batch = Lists.newArrayListWithCapacity(batchSize);

                    Callable<Void> task;
                    for (int i = 0; i < batchSize && (task = taskQueue.get().poll()) != null; i++) {
                        batch.add(task);
                        activeTaskCount.get().decrementAndGet();
                    }

                    if (!batch.isEmpty()) {
                        batchIndex++;
                        logger.info("Processing batch {}/{} (size={})", new Object[]{batchIndex, activeTaskCount.get(), batch.size()});

                        List<CompletableFuture<Void>> futures = Lists.newArrayListWithCapacity(batchSize);
                        for (Callable<Void> t : batch) {
                            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                                try {
                                    t.call();
                                } catch (Exception e) {
                                    logger.error("Error in batch task", e);
                                }
                            }, TtlExecutors.getTtlExecutorService(executorService));
                            futures.add(future);
                        }

                        // 等待本批次完成（带剩余时间超时）
                        long remaining = overallDeadline - System.currentTimeMillis();
                        if (remaining <= 0) {
                            throw new TimeoutException("Overall timeout reached");
                        }
                        try {
                            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                                    .get(remaining, TimeUnit.MILLISECONDS);
                        } catch (ExecutionException e) {
                            logger.warn("Batch execution error", e.getCause());
                        }

                        Thread.sleep(10);

                    } else {
                        // 队列暂时为空，检查是否可以退出

                        boolean noActiveTasks = activeTaskCount.get().intValue() == 0;
                        long idleDuration = System.currentTimeMillis() - lastTaskSubmitTime.get();
                        boolean isIdleLongEnough = idleDuration >= IDLE_TIMEOUT_MS;

                        if (noActiveTasks && isIdleLongEnough) {
                            remove();
                            logger.info("Idle for {} ms and no active tasks. Exiting. Total batches: {}",
                                    IDLE_TIMEOUT_MS, batchIndex);
                            break;
                        }

                        // 检查总超时
                        if (System.currentTimeMillis() > overallDeadline) {
                            remove();
                            throw new TimeoutException("Timeout waiting for tasks to complete");
                        }

                        // 短暂休眠，避免忙等待
                        Thread.sleep(100);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public static synchronized Set<String> excludeKeys(Set<String> keys) {
        if (excludeKeys.get() == null) {
            excludeKeys.set(Sets.newConcurrentHashSet());
        }
        excludeKeys.get().addAll(keys);
        return excludeKeys.get();
    }

    public static Set<String> excludeKeys() {
        return excludeKeys.get();
    }

    public static synchronized void setExportDependency(boolean export) {
        exportDependency.set(export);
    }

    public static void setEnableThread(boolean thread) {
        enableThread.set(thread);
    }

    public static boolean getEnableThread() {
        return BooleanUtils.isTrue(enableThread.get());
    }

    public static boolean exportDependency() {
        return BooleanUtils.isTrue(exportDependency.get());
    }

    public static synchronized void submitExportTask(Callable callable) {
        if (taskQueue.get() == null) {
            taskQueue.set(new ConcurrentLinkedQueue<Callable<Void>>());
            activeTaskCount.set(new AtomicInteger(0));
            lastTaskSubmitTime.set(System.currentTimeMillis());
        }
        taskQueue.get().add(callable);
        activeTaskCount.get().incrementAndGet();
        lastTaskSubmitTime.set(System.currentTimeMillis());

        //        if (callables.get() == null) {
//            callables.set(Collections.synchronizedList(Lists.newArrayList()));
//        }
//        callables.get().add(callable);
    }


    public static void remove() {
        keys.remove();
        excludeKeys.remove();
        exportDependency.remove();
        callables.remove();
        taskQueue.remove();
    }


}
