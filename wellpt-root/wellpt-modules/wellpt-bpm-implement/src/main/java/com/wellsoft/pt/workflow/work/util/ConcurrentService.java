package com.wellsoft.pt.workflow.work.util;

import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheConfiguration;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.cache.jedis.JedisCacheHolder;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.concurrent.*;

/**
 * 并发工具类
 *
 * @author liuxj
 * @since 2022-05-13
 */
@Service
public class ConcurrentService implements InitializingBean {

    /**
     * 存储执行任务的缓存名称
     */
    private final static String CACHE_NAME_THREAD = "sc";
    /**
     * 存储执行阻塞队列的前缀
     */
    private final static String BLOCK_PREFIX = "block:";
    /**
     * 等待结果返回时间,单位秒
     */
    private final static int RESULT_TIME_OUT = 10;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private Jedis jedis;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private ThreadPoolTaskExecutor taskPoolExecutor;
    @Autowired
    private CacheConfiguration cacheConfiguration;
    private ConcurrentHashMap concurrentHashMap;
    /**
     * 是否存在redis
     */
    private boolean isExistRedis = true;

    /**
     * 是否存在redis
     *
     * @return
     */
    private boolean isExistRedis() {
        Boolean isExistRedis = Boolean.FALSE;
        try {
            if (StringUtils.isNotBlank(cacheConfiguration.getRedisHost())) {
                isExistRedis = true;
            }
        } catch (Exception e) {
            logger.error("获取redis配置参数失败：", e);
        } finally {
            return isExistRedis;
        }
    }

    /**
     * 通过本地异步执行任务
     *
     * @param callable 需要执行的任务
     * @param id
     * @param <T>
     */
    public <T> void doJobByLocal(Callable<T> callable, String id) {
        Future<T> future = taskPoolExecutor.submit(callable);
        concurrentHashMap.put(id, future);
    }

    /**
     * 执行异步执行任务
     *
     * @param callable 需要执行的任务
     * @param id
     * @param <T>
     */
    public <T> void doJob(Callable<T> callable, String id) {
        if (isExistRedis()) {
            doJobByRedis(callable, id);
        } else {
            doJobByLocal(callable, id);
        }
    }

    /**
     * 通过本地缓存获取结果
     * No qualifying bean of type
     *
     * @param id  标识符
     * @param <T>
     * @return
     */
    public <T> T getResultByLocal(String id) {
        try {
            Future<T> future = (Future<T>) concurrentHashMap.get(id);
            if (future == null) {
                return null;
            }
            T t = future.get(RESULT_TIME_OUT, TimeUnit.SECONDS);
            concurrentHashMap.remove(id);
            return t;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("执行异步任务中断异常：请求id, 线程名称{}", id, Thread.currentThread().getName());
            logger.error("执行异步任务中断异常：", e);
            return null;
        } catch (TimeoutException e) {
            logger.error("执行异步任务超时中断异常：请求id, 线程名称{}", id, Thread.currentThread().getName());
            logger.error("执行异步任务超时中断异常：", e);
            return null;
        }
    }

    /**
     * 通过redis异步执行任务
     *
     * @param callable 需要执行的任务
     * @param id
     * @param <T>
     */
    public <T> void doJobByRedis(Callable<T> callable, String id) {

        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
            try {
                return callable.call();
            } catch (InterruptedException e1) {
                logger.error("执行异步任务中断异常：请求id, 线程名称{}", id, Thread.currentThread().getName());
            } catch (Exception e) {
                logger.error("执行异步任务失败：请求id{}, 线程名称{}", id, Thread.currentThread().getName());
            }
            return null;
        }, taskPoolExecutor).whenComplete((v, e) -> {
            // 执行完成没有错误信息
            try {
                if (e == null) {
                    jedis.del(BLOCK_PREFIX + id);
                    Cache cache = (Cache) cacheManager.getCache(CACHE_NAME_THREAD);
                    if (cache != null) {
                        cache.put(id, v);
                        jedis.lpush(BLOCK_PREFIX + id, "ok");
                    }
                } else {
                    logger.error("执行异步任务失败：请求id{}, 错误：{}", id, e);
                }
            } catch (Exception e2) {
                logger.error("保存数据到缓存失败", e2);
            }
        });
    }

    /**
     * 存在redis时获取结果
     *
     * @param id  标识符
     * @param <T>
     * @return
     */
    public <T> T getResult(String id) {
        if (isExistRedis()) {
            return getResultByRedis(id);
        } else {
            return getResultByLocal(id);
        }
    }

    /**
     * 存在redis时获取结果
     *
     * @param id  标识符
     * @param <T>
     * @return
     */
    public <T> T getResultByRedis(String id) {
        T result = null;
        try {
            // 先直接从缓存获取
            Cache cache = (Cache) cacheManager.getCache(CACHE_NAME_THREAD);
            Cache.ValueWrapper valueWrapper = cache.get(id);
            if (valueWrapper != null && valueWrapper.get() != null) {
                result = (T) valueWrapper.get();
                cache.evict(id);
                jedis.del(BLOCK_PREFIX + id);
                return result;
            } else {
                // 从阻塞队列中获取
                try {
                    jedis.blpop(RESULT_TIME_OUT, BLOCK_PREFIX + id);
                    jedis.del(BLOCK_PREFIX + id);
                    valueWrapper = cache.get(id);
                    if (valueWrapper != null && valueWrapper.get() != null) {
                        result = (T) valueWrapper.get();
                        cache.evict(id);
                        return result;
                    }
                } catch (Exception e) {
                    logger.error("获取线程结果失败：请求id:{},错误{}", id, e.getMessage());
                }
                return null;
            }

        } catch (Exception e2) {
            logger.error("从缓存获取数据失败", e2);
        } finally {
            return result;
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        isExistRedis = isExistRedis();
        if (!isExistRedis) {
            concurrentHashMap = new ConcurrentHashMap();
        } else {
            jedis = JedisCacheHolder.jedis(cacheConfiguration.jedisConnectionFactory(
                    cacheConfiguration.redisClusterConfiguration(), cacheConfiguration.jedispoolConfig()));
        }

    }
}
