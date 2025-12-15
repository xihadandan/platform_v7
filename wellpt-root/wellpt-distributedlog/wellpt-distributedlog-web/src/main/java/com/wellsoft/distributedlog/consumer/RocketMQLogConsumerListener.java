package com.wellsoft.distributedlog.consumer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.distributedlog.Constants;
import com.wellsoft.distributedlog.es.entity.LogEntity;
import com.wellsoft.distributedlog.es.service.LogService;
import com.wellsoft.distributedlog.utils.GsonUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * Description: 日志消费队列侦听
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年07月01日   chenq	 Create
 * </pre>
 */
public class RocketMQLogConsumerListener implements MessageListenerConcurrently {

    LogService logService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public RocketMQLogConsumerListener(LogService logService) {
        this.logService = logService;
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        if (!logService.ping()) {
            logger.error("无法连接elasticsearch服务 ... ");
            return ConsumeConcurrentlyStatus.RECONSUME_LATER; // 无连接es服务，则通知队列服务延后消费
        }
        Map<String, List<IndexQuery>> map = Maps.newHashMap();
        for (MessageExt s : msgs) {
            LogEntity logEntity = GsonUtils.fromJson(new String(s.getBody()), LogEntity.class);
            String indexName = Constants.ES_LOG_INDEX_NAME_PREFIX + DateFormatUtils.format(logEntity.getLogTime(), Constants.LOG_INDEX_DATE_FORMATE);
            if (!map.containsKey(indexName)) {
                map.put(indexName, Lists.<IndexQuery>newArrayList());
            }
            map.get(indexName).add(new IndexQueryBuilder()
                    .withObject(logEntity)
                    .build());
        }
        try {
            logService.bulkIndex(map); // 索引文档
        } catch (Exception e) {
            logger.error("mq消费异常：", e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
