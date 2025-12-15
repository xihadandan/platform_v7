package com.wellsoft.pt.session.config;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Description: 启用spring-session机制，session托管于redis缓存服务器，实现session共享
 *
 * @author chenq
 * @date 2019-02-18
 * <p/>
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019-02-18    chenq		2019-02-18		Create
 * </pre>
 */
//session超时时间设置会在SessionTimeoutRefreshListener进行刷新
@EnableRedisHttpSession
@Configuration
public class SpringSessionConfig implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(SpringSessionConfig.class);

    @Value("${session.redis.nodes}")
    private String redisNodes;

    @Value("${session.jedis.pool.max-total}")
    private Integer jedisPoolMaxTotal;

    @Value("${session.jedis.pool.min-idle}")
    private Integer jedisPoolMinIdle;

    @Value("${session.jedis.pool.max-idle}")
    private Integer jedisPollMaxIdle;

    @Value("${session.redis.master:}")
    private String redisMasterName;

    @Value("${session.redis.password:}")
    private String password;

    @Bean(name = "sessionJedisConnectionFactory")
    @Primary
    public JedisConnectionFactory jedisConnectionFactory(
            @Qualifier("sessionClusterConfiguration") RedisClusterConfiguration clusterConfiguration,
            @Qualifier("sessionJedisPoolConfig") JedisPoolConfig poolConfig,
            @Qualifier("sessionSentinelConfiguration") RedisSentinelConfiguration sentinelConfiguration) {
        return build(clusterConfiguration, poolConfig, sentinelConfiguration);
    }

    private JedisConnectionFactory build(RedisClusterConfiguration clusterConfiguration,
                                         JedisPoolConfig poolConfig,
                                         RedisSentinelConfiguration sentinelConfiguration) {
        JedisConnectionFactory jedisConnectionFactory = null;
        if (!clusterConfiguration.getClusterNodes().isEmpty()) {//redis-cluster集群配置优先
            jedisConnectionFactory = new JedisConnectionFactory(clusterConfiguration, poolConfig);
            if (StringUtils.isNotBlank(password)) {
                jedisConnectionFactory.setPassword(password);
            }
            return jedisConnectionFactory;
        }
        if (!sentinelConfiguration.getSentinels().isEmpty()) {//redis-sentinel集群配置次之
            jedisConnectionFactory = new JedisConnectionFactory(sentinelConfiguration, poolConfig);
            if (StringUtils.isNotBlank(password)) {
                jedisConnectionFactory.setPassword(password);
            }
            return jedisConnectionFactory;
        }

        //单机redis
        return singleJedisConnectionFactory(poolConfig);
    }

    @Bean(name = "dataJedisConnectionFactory")
    public JedisConnectionFactory dataJedisConnectionFactory(
            @Qualifier("sessionClusterConfiguration") RedisClusterConfiguration clusterConfiguration,
            @Qualifier("sessionJedisPoolConfig") JedisPoolConfig poolConfig,
            @Qualifier("sessionSentinelConfiguration") RedisSentinelConfiguration sentinelConfiguration) {
        return build(clusterConfiguration, poolConfig, sentinelConfiguration);
    }


    private JedisConnectionFactory singleJedisConnectionFactory(JedisPoolConfig poolConfig) {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(poolConfig);
        String[] parts = redisNodes.split(":");
        connectionFactory.setHostName(parts[0].trim());
        connectionFactory.setPort(Integer.parseInt(parts[1].trim()));
        if (StringUtils.isNotBlank(password)) {
            connectionFactory.setPassword(password);
        }
        return connectionFactory;
    }

    @Bean(name = "sessionJedisPoolConfig")
    public JedisPoolConfig jedispoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(jedisPoolMaxTotal);
        poolConfig.setMinIdle(jedisPoolMinIdle);
        poolConfig.setMaxIdle(jedisPollMaxIdle);
        return poolConfig;
    }

    @Bean(name = "sessionClusterConfiguration")
    public RedisClusterConfiguration clusterConfiguration() {
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
        if (StringUtils.isNotBlank(redisMasterName)) {//redis-sentinel集群方式
            return clusterConfiguration;
        }
        clusterConfiguration.setMaxRedirects(6);
        if (StringUtils.isBlank(redisNodes)) {
            throw new RuntimeException("spring-session解析redis配置，redis.host配置not found");
        }
        String[] hosts = redisNodes.split(",");
        if (hosts.length > 1 && hosts.length < 6) {
            throw new RuntimeException(
                    "spring-session解析redis集群配置，redis服务器至少6台，目前配置数:[" + hosts.length + "]");
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

    @Bean(name = "sessionSentinelConfiguration")
    public RedisSentinelConfiguration sentinelConfiguration() {
        RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration();
        if (org.apache.commons.lang.StringUtils.isBlank(redisMasterName)) {//redis-cluster集群方式
            return sentinelConfiguration;
        }
        sentinelConfiguration.master(this.redisMasterName);
        String[] hosts = this.redisNodes.split(",");
        for (String h : hosts) {
            String[] parts = h.split(":");
            sentinelConfiguration.sentinel(
                    new RedisNode(parts[0].trim(), Integer.parseInt(parts[1].trim())));
        }
        return sentinelConfiguration;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info(">>> 启用spring-session&redis会话管理 <<<");
        logger.info(">>> session-redisNodes: {} <<<", this.redisNodes);
        logger.info(">>> session-redisMaster: master={}  <<<", this.redisMasterName);
        logger.info(">>> session-jedisPoolConfig: maxTotal={},maxIdle={},minIdle={} <<<",
                new Object[]{this.jedisPoolMaxTotal, this.jedisPollMaxIdle, this.jedisPoolMinIdle});

    }

}

