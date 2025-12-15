package com.wellsoft.pt.cache.jedis;

import com.google.common.base.Stopwatch;
import com.wellsoft.context.util.serialization.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Description: jedis缓存操作
 *
 * @author chenq
 * @date 2019/2/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/2/13    chenq		2019/2/13		Create
 * </pre>
 */
public class JedisCache implements Cache {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisCache.class);
    private JedisConnectionFactory jedisConnectionFactory;
    private String name;
    private String keyPrefix;


    public JedisCache(String name, JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
        this.name = name;
        this.keyPrefix = name + ":";
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.jedisConnectionFactory.getConnection().getNativeConnection();
    }


    @Override
    public ValueWrapper get(Object o) {
        try {
            String key = getCacheKey(o);
            byte[] bytes = JedisCacheHolder.jedis(this.jedisConnectionFactory).get(key.getBytes());
            if (bytes != null) {
                return new SimpleValueWrapper(
                        SerializationUtils.deserialization(bytes));
            } else {
                LOGGER.warn("key=[{}] cache not found !", key);
            }
        } catch (Exception e) {
            LOGGER.error("jedis->get 异常：", e);
        } finally {
            JedisCacheHolder.close();
        }
        return null;
    }

    @Override
    public <T> T get(Object o, Class<T> type) {
        try {
            String key = getCacheKey(o);
            byte[] bytes = JedisCacheHolder.jedis(this.jedisConnectionFactory).get(key.getBytes());
            if (bytes != null) {
                return SerializationUtils.deserialization(bytes);
            } else {
                LOGGER.warn("key=[{}] cache not found !", key);
            }
        } catch (Exception e) {
            LOGGER.error("jedis->get 异常：", e);
        } finally {
            JedisCacheHolder.close();
        }
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        try {
            if (exists(key)) {//
                return (T) get(key).get();
            }
            T value = valueLoader.call();
            put(key, value);
            return (T) get(key).get();
        } catch (Exception e) {
            LOGGER.warn("key=[{}] cache not found !", key);
        }
        return null;
    }

    @Override
    public void put(Object o, Object value) {
        Stopwatch timer = Stopwatch.createStarted();
        try {
            Assert.notNull(value, "value not null!");
            String key = getCacheKey(o);
            JedisCacheHolder.jedis(this.jedisConnectionFactory).set(key.getBytes(),
                    SerializationUtils.serializationObject((Serializable) value));
            LOGGER.info("key=[{}] put cache succeed ! time={} !", key, timer.stop());
        } catch (Exception e) {
            LOGGER.error("jedis->put 异常：", e);
        } finally {
            JedisCacheHolder.close();
        }

    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        put(key, value);
        return get(key);
    }


    public Boolean exists(Object o) {
        try {
            String key = getCacheKey(o);
            return JedisCacheHolder.jedis(this.jedisConnectionFactory).exists(key.getBytes());
        } catch (Exception e) {
            LOGGER.error("jedis->exists 异常：", e);
        } finally {
            JedisCacheHolder.close();
        }
        return false;
    }

    @Override
    public void evict(Object o) {
        try {
            String key = getCacheKey(o);
            JedisCacheHolder.jedis(this.jedisConnectionFactory).del(key.getBytes());
            LOGGER.info("key=[{}] cache del succeed !", key);
        } catch (Exception e) {
            LOGGER.error("jedis->evict 异常：", e);
        } finally {
            JedisCacheHolder.close();
        }
    }

    @Override
    public void clear() {
        try {
            Set<byte[]> keys = JedisCacheHolder.jedis(this.jedisConnectionFactory).keys(
                    getCacheKey("*").getBytes());
            JedisCacheHolder.close();
            for (byte[] b : keys) {
                try {
                    JedisCacheHolder.jedis(this.jedisConnectionFactory).del(b);
                } finally {
                    JedisCacheHolder.close();
                }
            }
        } catch (Exception e) {
            LOGGER.error("jedis->clear 异常：", e);
        }
    }


    public String getCacheKey(Object in) {
        return keyPrefix + (String) in;
    }
}
