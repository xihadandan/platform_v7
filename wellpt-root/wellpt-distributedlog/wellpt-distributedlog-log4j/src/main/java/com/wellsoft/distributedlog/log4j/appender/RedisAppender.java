package com.wellsoft.distributedlog.log4j.appender;

import com.google.common.base.Throwables;
import com.wellsoft.distributedlog.Constants;
import com.wellsoft.distributedlog.jedis.JedisCommandsFactory;
import com.wellsoft.distributedlog.jedis.JedisConfig;
import com.wellsoft.distributedlog.jedis.JedisUtil;
import redis.clients.jedis.JedisCommands;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年06月29日   chenq	 Create
 * </pre>
 */
public class RedisAppender extends AbstractLogQueueAppender {

    private String redisNodes;

    private String masterName;

    private String password;

    private Integer poolMaxTotal;

    private Integer poolMinIdle;

    private Integer poolMaxIdle;


    @Override
    protected void subAppend(List<String> logs) {
        try {
            JedisCommands commands = JedisCommandsFactory.jedisCommands(new JedisConfig(this.getRedisNodes(),
                    poolMaxTotal, poolMinIdle, poolMaxIdle, getMasterName(), getPassword()));
            JedisUtil.rPush(commands, Constants.LOG_REDIS_KEY, logs);
        } catch (Exception e) {
            throw new RuntimeException("日志写入redis失败: " + Throwables.getStackTraceAsString(e));
        }
    }

    public String getRedisNodes() {
        return redisNodes;
    }

    public void setRedisNodes(String redisNodes) {
        this.redisNodes = redisNodes;
    }

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(String masterName) {
        this.masterName = masterName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
