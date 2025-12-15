package com.wellsoft.distributedlog.jedis;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * Description: jedis工具栏，封装redis的交互api
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年06月30日   chenq	 Create
 * </pre>
 */
public class JedisUtil {


    public static void rPush(JedisCommands commands, String key, Collection<String> strings) {
        commands.rpush(key, strings.toArray(new String[]{}));
        if (commands instanceof Jedis) {
            ((Jedis) commands).close();
        }
    }

    private static byte[][] toBytes(Collection<String> strings) {
        byte[][] bytes = new byte[strings.size()][];
        Iterator<String> iterator = strings.iterator();
        int i = 0;
        while ((iterator.hasNext())) {
            bytes[i++] = iterator.next().getBytes();
        }
        return bytes;
    }

    public static List<String> lPop(JedisCommands commands, String key, int size) {
        List<String> pops = new ArrayList<String>(size);
        while (size > 0) {
            String rs = commands.lpop(key);
            if (StringUtils.isBlank(rs)) {
                break;
            }
            pops.add(rs);
            size--;
        }
        if (commands instanceof Jedis) {
            ((Jedis) commands).close();
        }
        return pops;
    }


}
