package com.wellsoft.pt.cache.jedis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;

/**
 * Description:
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
public class JedisCacheManager extends AbstractCacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(JedisCacheManager.class);

    protected JedisConnectionFactory jedisConnectionFactory;


    public JedisCacheManager() {
    }

    public JedisCacheManager(
            JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return Collections.emptyList();
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = super.getCache(name);
        if (cache == null) {
            cache = new JedisCache(name, this.jedisConnectionFactory);
            this.addCache(cache);
        }
        return cache;
    }


    @Override
    public void afterPropertiesSet() {
        Assert.notNull(this.jedisConnectionFactory, "jedisConnectionFactory is required");
        try {
            this.jedisConnectionFactory.getConnection().close();
        } catch (RedisConnectionFailureException e) {
            LOGGER.error("无法获取redis连接！", e);
            throw new RuntimeException(e);
        }
    }
}
