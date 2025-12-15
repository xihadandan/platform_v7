package com.wellsoft.distributedlog.jedis;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Arrays;
import java.util.Set;

/**
 * Description: redis命令工厂
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年07月02日   chenq	 Create
 * </pre>
 */
public class JedisCommandsFactory {

    private static JedisCluster jedisCluster;

    private static JedisSentinelPool jedisSentinelPool;

    private static JedisPool jedisPool;

    public static JedisCommands jedisCommands(JedisConfig config) {
        JedisCluster jedisCluster = jedisCluster(config);
        if (jedisCluster != null) {
            return jedisCluster;
        }
        JedisSentinelPool jedisSentinelPool = jedisSentinelPool(config);
        if (jedisSentinelPool != null) {
            return jedisSentinelPool.getResource();
        }
        return jedisPool(config).getResource();
    }

    private static JedisCluster jedisCluster(JedisConfig config) {
        if (StringUtils.isBlank(config.getRedisNodes())) {
            return null;
        }
        String[] nodes = config.getRedisNodes().split(",|;");
        if (nodes.length == 1) {
            return null;
        }
        if (nodes.length > 1 && nodes.length < 6) {
            throw new RuntimeException(
                    "解析redis集群配置，redis服务器至少6台，目前配置数:[" + nodes.length + "]");
        }
        if (jedisCluster == null) {
            synchronized (JedisCluster.class) {
                if (jedisCluster == null) {
                    Set<HostAndPort> hostAndPorts = Sets.newHashSet();
                    for (String n : nodes) {
                        String[] hp = n.split(":");
                        hostAndPorts.add(new HostAndPort(hp[0], Integer.parseInt(hp[1])));
                    }
                    jedisCluster = new JedisCluster(hostAndPorts, generatePoolConfig(config));
                }
            }
        }
        return jedisCluster;
    }

    private static JedisSentinelPool jedisSentinelPool(JedisConfig config) {
        if (StringUtils.isBlank(config.getRedisNodes()) || StringUtils.isBlank(config.getMasterName())) {
            return null;
        }
        if (jedisSentinelPool == null) {
            synchronized (JedisSentinelPool.class) {
                if (jedisSentinelPool == null) {

                    jedisSentinelPool = new JedisSentinelPool(config.getMasterName(),
                            Sets.newLinkedHashSet(Arrays.asList(config.getRedisNodes().split(",|;"))),
                            generatePoolConfig(config), config.getTimemout(), config.getPassword(), config.getDatabase());
                }
            }
        }
        return jedisSentinelPool;
    }

    private static JedisPool jedisPool(JedisConfig config) {
        if (StringUtils.isBlank(config.getRedisNodes())) {
            return null;
        }
        if (jedisPool == null) {
            synchronized (JedisPool.class) {
                if (jedisPool == null) {
                    String[] hp = config.getRedisNodes().split(":");
                    jedisPool = new JedisPool(generatePoolConfig(config), hp[0], Integer.parseInt(hp[1]),
                            config.getTimemout(), config.getPassword(), config.getDatabase());
                }
            }
        }
        return jedisPool;
    }

    private static GenericObjectPoolConfig generatePoolConfig(JedisConfig config) {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(config.getPoolMaxIdle());
        poolConfig.setMaxTotal(config.getPoolMaxTotal());
        poolConfig.setMinIdle(config.getPoolMinIdle());
        return poolConfig;
    }
}
