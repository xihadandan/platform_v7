package com.wellsoft.distributedlog.consumer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.distributedlog.Constants;
import com.wellsoft.distributedlog.es.entity.LogEntity;
import com.wellsoft.distributedlog.es.service.LogService;
import com.wellsoft.distributedlog.jedis.JedisCommandsFactory;
import com.wellsoft.distributedlog.jedis.JedisConfig;
import com.wellsoft.distributedlog.jedis.JedisUtil;
import com.wellsoft.distributedlog.utils.GsonUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisCommands;

import java.util.List;
import java.util.Map;

/**
 * Description: redis日志消费者
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年06月30日   chenq	 Create
 * </pre>
 */
public class RedisLogConsumer implements Runnable {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    JedisConfig jedisConfig;
    LogService logService;
    int batchSize = 100;

    public RedisLogConsumer(JedisConfig jedisConfig, LogService logService) {
        this.jedisConfig = jedisConfig;
        this.logService = logService;
    }

    public RedisLogConsumer(JedisConfig jedisConfig, LogService logService, int batchSize) {
        this.jedisConfig = jedisConfig;
        this.logService = logService;
        this.batchSize = batchSize;
    }


    @Override
    public void run() {
        try {
            if (!logService.ping()) {
                logger.error("无法连接elasticsearch服务 ... ");
                return;
            }
            JedisCommands jedisCommands = JedisCommandsFactory.jedisCommands(this.jedisConfig);
            List<String> strings = JedisUtil.lPop(jedisCommands, Constants.LOG_REDIS_KEY, batchSize);
            logger.info("start pool redis log queue , size : [{}]", strings.size());
            if (CollectionUtils.isEmpty(strings)) {
                return;
            }
            Map<String, List<IndexQuery>> map = Maps.newHashMap();
            for (String s : strings) {
                LogEntity logEntity = GsonUtils.fromJson(s, LogEntity.class);
                String indexName = Constants.ES_LOG_INDEX_NAME_PREFIX + DateFormatUtils.format(logEntity.getLogTime(), Constants.LOG_INDEX_DATE_FORMATE);
                if (!map.containsKey(indexName)) {
                    map.put(indexName, Lists.<IndexQuery>newArrayList());
                }
                map.get(indexName).add(new IndexQueryBuilder()
                        .withObject(logEntity)
                        .build());
            }
            logService.bulkIndex(map);
        } catch (Exception e) {
            logger.error("消费redis日志异常：", e);
        }

    }
}
