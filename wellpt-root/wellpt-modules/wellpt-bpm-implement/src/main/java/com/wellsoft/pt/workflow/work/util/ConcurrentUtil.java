package com.wellsoft.pt.workflow.work.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 并发工具类
 *
 * @author liuxj
 * @since 2022-05-13
 */
public class ConcurrentUtil {

    private static final Logger logger = LoggerFactory.getLogger(ConcurrentUtil.class);

    private static ConcurrentHashMap<String, Object> concurrentHashMap = new ConcurrentHashMap();

    /**
     * 异步执行任务
     *
     * @param taskExecutor 线程池
     * @param callable     需要执行的任务
     * @param id
     * @param <T>
     */
    public static <T> void doJob(ThreadPoolTaskExecutor taskExecutor, Callable<T> callable, String id) {
        Future<T> future = taskExecutor.submit(callable);
        concurrentHashMap.put(id, future);
    }

    /**
     * 获取结果
     *
     * @param id  标识符
     * @param <T>
     * @return
     */
    public static <T> T getResult(String id) {
        try {
            Future<T> future = (Future<T>) concurrentHashMap.get(id);
            T t = future.get();
            concurrentHashMap.remove(id);
            return t;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("获取结果异常", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取结果集，执行时会阻塞直到有结果，中间的异常不会被静默
     *
     * @param future Future
     * @param <T>    返回的结果集泛型
     * @return T
     */
    public static <T> T futureGet(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            logger.error("获取结果集异常", e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
