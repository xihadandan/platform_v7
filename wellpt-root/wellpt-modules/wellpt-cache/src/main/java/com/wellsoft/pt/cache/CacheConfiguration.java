package com.wellsoft.pt.cache;

import com.wellsoft.pt.cache.jedis.JedisCacheManager;
import com.wellsoft.pt.cache.support.CompositeJedisAndEhCacheManager;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/11/7
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/11/7    chenq		2019/11/7		Create
 * </pre>
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${redis.host:}")
    private String redisHost;
    @Value("${redis.port:}")
    private String redisPort;
    @Value("${redis.password:}")
    private String password;
    @Value("${redis.master:}")
    private String redisMasterName;
    @Value("${redis.dbIndex:}")
    private String redisDbIndex;


    @Value("${jedis.pool.maxTotal:100}")
    private int jedisPoolMaxTotal;

    @Value("${jedis.pool.maxIdle:30}")
    private int jedisPoolMaxIdle;

    @Value("${jedis.pool.minIdle:10}")
    private int jedisPoolMinIdle;

    @Bean
    public CompositeCacheManager compositeCacheManager(@Qualifier(value = "jedisConnectionFactory") JedisConnectionFactory jedisConnectionFactory) {
        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();

        List<CacheManager> list = Lists.newArrayList();
        EhCacheCacheManager ehCacheManager = ehCacheCacheManager();
        JedisCacheManager jedisCacheManager = null;
        if (StringUtils.isNotBlank(redisHost)) {
            jedisCacheManager = new JedisCacheManager(jedisConnectionFactory);
        }

        if (jedisCacheManager != null && ehCacheManager != null) {
            list.add(new CompositeJedisAndEhCacheManager(jedisCacheManager, ehCacheManager));
        } else if (jedisCacheManager != null) {
            list.add(jedisCacheManager);
        } else if (ehCacheManager != null) {
            list.add(ehCacheManager);
        } else {
            list.add(new ConcurrentMapCacheManager());
        }
        compositeCacheManager.setCacheManagers(list);
        return compositeCacheManager;
    }


    @Bean
    public JedisConnectionFactory jedisConnectionFactory(@Qualifier(value = "redisClusterConfiguration") RedisClusterConfiguration redisClusterConfiguration,
                                                         @Qualifier(value = "jedispoolConfig") JedisPoolConfig jedispoolConfig) {
        if (StringUtils.isNotBlank(redisHost)) {
            JedisConnectionFactory jedisConnectionFactory = null;
            if (!redisClusterConfiguration.getClusterNodes().isEmpty()) {//redis-cluster集群配置优先
                jedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration, jedispoolConfig);
                if (StringUtils.isNotBlank(password)) {
                    jedisConnectionFactory.setPassword(password);
                }
                if (StringUtils.isNotBlank(redisDbIndex)) {
                    jedisConnectionFactory.setDatabase(Integer.parseInt(redisDbIndex));
                }
                return jedisConnectionFactory;
            }
            //FIXME: sentinel
            /*if (!redisSentinelConfiguration.getSentinels().isEmpty()) {//redis-sentinel集群配置次之
                jedisConnectionFactory = new JedisConnectionFactory(redisSentinelConfiguration, jedispoolConfig);
                if (StringUtils.isNotBlank(password)) {
                    jedisConnectionFactory.setPassword(password);
                }
                jedisConnectionFactory.afterPropertiesSet();
                return new JedisCacheManager(jedisConnectionFactory);
            }*/

            //单机redis
            return singleJedisConnectionFactory(jedispoolConfig);
        }
        return new JedisConnectionFactory();
    }

    @Bean
    @Primary
    public JedisPoolConfig jedispoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(jedisPoolMaxTotal);
        poolConfig.setMinIdle(jedisPoolMinIdle);
        poolConfig.setMaxIdle(jedisPoolMaxIdle);
        return poolConfig;
    }


    @Bean
    public RedisClusterConfiguration redisClusterConfiguration() {
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
        if (StringUtils.isNotBlank(redisMasterName)) {//redis-sentinel集群方式
            return clusterConfiguration;
        }
        if (StringUtils.isBlank(redisHost)) {
            return clusterConfiguration;
        }
        clusterConfiguration.setMaxRedirects(6);
        String[] hosts = redisHost.split(",");
        if (hosts.length > 1 && hosts.length < 6) {
            throw new RuntimeException(
                    "解析redis集群配置，redis服务器至少6台，目前配置数:[" + hosts.length + "]");
        }

        if (hosts.length != 1) {//集群至少6台redis服务器
            for (String h : hosts) {//添加集群节点
                String[] parts = h.split(":");
                clusterConfiguration.clusterNode(
                        new RedisNode(parts[0].trim(), Integer.parseInt(parts[1].trim())));
            }
            clusterConfiguration.setMaxRedirects(hosts.length);//最大重定向次数
        }
        return clusterConfiguration;
    }

    /*@Bean
    public RedisSentinelConfiguration redisSentinelConfiguration() {
        RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration();
        if (StringUtils.isBlank(redisMasterName)) {//redis-cluster集群方式
            return sentinelConfiguration;
        }
        sentinelConfiguration.master(this.redisMasterName);
        String[] hosts = this.redisHost.split(",");
        if (hosts.length < 2) {// 哨兵至少两台:一主一备
            return sentinelConfiguration;
        }
        for (String h : hosts) {
            String[] parts = h.split(":");
            sentinelConfiguration.sentinel(
                    new RedisNode(parts[0].trim(), Integer.parseInt(parts[1].trim())));
        }
        return sentinelConfiguration;
    }*/


    private JedisConnectionFactory singleJedisConnectionFactory(JedisPoolConfig poolConfig) {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(poolConfig);

        if (redisHost.indexOf(":") != -1) {
            String[] parts = redisHost.split(":");
            connectionFactory.setHostName(parts[0].trim());
            connectionFactory.setPort(Integer.parseInt(parts[1].trim()));
        } else {
            connectionFactory.setHostName(redisHost);
            connectionFactory.setPort(Integer.parseInt(redisPort));
        }
        if (StringUtils.isNotBlank(password)) {
            connectionFactory.setPassword(password);
        }
        if (StringUtils.isNotBlank(redisDbIndex)) {
            connectionFactory.setDatabase(Integer.parseInt(redisDbIndex));
        }
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }


    private EhCacheCacheManager ehCacheCacheManager() {
        ClassPathResource echacheXml = new ClassPathResource("ehcache.xml");
        if (echacheXml.exists()) {
            try {
                return new DynamicEhCacheCacheManager(net.sf.ehcache.CacheManager.create(echacheXml.getURL()));
            } catch (Exception e) {
            }
        }
        return null;
    }

    /**
     * 动态创建不存在的缓存管理器
     */
    private class DynamicEhCacheCacheManager extends EhCacheCacheManager {
        public DynamicEhCacheCacheManager(net.sf.ehcache.CacheManager cacheManager) {
            super(cacheManager);
        }

        @Override
        protected org.springframework.cache.Cache getMissingCache(String name) {
            Cache cache = super.getMissingCache(name);
            if (cache == null) {
                return new EhCacheCache(getCacheManager().addCacheIfAbsent(name));
            }
            return cache;
        }
    }

    @Bean
    @Primary
    public com.wellsoft.pt.cache.CacheManager cacheManager(CompositeCacheManager compositeCacheManager,
                                                           CacheAspectSupport cacheAspectSupport) {
        com.wellsoft.pt.cache.CacheManager cacheManager = new CacheManagerImpl();
        cacheManager.setCacheManager(compositeCacheManager);
        cacheAspectSupport.setKeyGenerator(new CustomKeyGenerator());
        return cacheManager;
    }

    @Bean
    public GuavaCacheManager guavaCacheManager() {
        return new GuavaCacheManager();
    }

    public String getRedisHost() {
        return redisHost;
    }
}
