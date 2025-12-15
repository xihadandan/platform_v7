package com.wellsoft.pt.cache.jedis;

import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;

/**
 * Description: jedis线程变量操作
 *
 * @author chenq
 * @date 2019/2/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/2/14    chenq		2019/2/14		Create
 * </pre>
 */
public class JedisCacheHolder {

    private static final ThreadLocal<JedisConnection> CONNECT = new ThreadLocal<>();

    public static Jedis jedis(JedisConnectionFactory factory) {
        CONNECT.set((JedisConnection) factory.getConnection());
        return CONNECT.get().getNativeConnection();
    }

    public static void close() {
        CONNECT.get().close();
    }
}
