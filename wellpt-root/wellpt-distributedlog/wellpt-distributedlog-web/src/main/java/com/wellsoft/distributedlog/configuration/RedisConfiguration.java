package com.wellsoft.distributedlog.configuration;

import com.wellsoft.distributedlog.consumer.RedisLogConsumer;
import com.wellsoft.distributedlog.es.service.LogService;
import com.wellsoft.distributedlog.jedis.JedisConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.scheduling.concurrent.ScheduledExecutorTask;

import java.util.concurrent.TimeUnit;

/**
 * Description: redis缓存配置。初始化redis连接池、redis调度任务
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年06月29日   chenq	 Create
 * </pre>
 */
@Configuration
public class RedisConfiguration {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${log.redis.redisNodes:}")
    private String redisNodes;
    @Value("${log.redis.poolMaxTotal:}")
    private String poolMaxTotal;
    @Value("${log.redis.poolMinIdle:}")
    private String poolMinIdle;
    @Value("${log.redis.poolMaxIdle:}")
    private String poolMaxIdle;
    @Value("${log.redis.masterName:}")
    private String masterName;
    @Value("${log.redis.password:}")
    private String password;
    @Value("${log.redis.database:}")
    private String database;
    @Value("${log.consume.period:500}")  // 默认500毫秒为拉取日志的间隔
    private String consumeLogPeriod;


    @Bean
    public ScheduledExecutorTask redisTask(LogService logService) {
        if (StringUtils.isBlank(redisNodes)) {
            logger.warn("未绑定redis服务节点信息");
            return null; // 无redis服务节点，则不采用redis服务队列消费日志
        }

        // redis 连接配置
        JedisConfig config = new JedisConfig(redisNodes,
                StringUtils.isNotBlank(poolMaxTotal) ? Integer.parseInt(poolMaxTotal) : null,
                StringUtils.isNotBlank(poolMinIdle) ? Integer.parseInt(poolMinIdle) : null,
                StringUtils.isNotBlank(poolMaxIdle) ? Integer.parseInt(poolMaxIdle) : null, masterName, password);

        // 启动redis任务
        ScheduledExecutorTask task = new ScheduledExecutorTask();
        task.setRunnable(new RedisLogConsumer(config, logService));
        task.setTimeUnit(TimeUnit.MILLISECONDS);
        task.setPeriod(Integer.parseInt(consumeLogPeriod));
        task.setDelay(3000);
        return task;
    }


    @Bean
    public ScheduledExecutorFactoryBean scheduledExecutorFactoryBean(@Qualifier("redisTask") ScheduledExecutorTask redisTask) {
        if (redisTask == null) {
            return null;  // 无redis消费任务，则不构建调度执行器
        }
        ScheduledExecutorFactoryBean bean = new ScheduledExecutorFactoryBean();
        bean.setScheduledExecutorTasks(redisTask);
        return bean;
    }


}
