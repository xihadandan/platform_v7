package com.wellsoft.distributedlog.configuration;

import com.wellsoft.distributedlog.consumer.RocketMQLogConsumerListener;
import com.wellsoft.distributedlog.es.service.LogService;
import com.wellsoft.distributedlog.lifecycle.RocketMQConsumerLifecycle;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import static com.wellsoft.distributedlog.Constants.ROCKETMQ_TAGS;
import static com.wellsoft.distributedlog.Constants.ROCKETMQ_TOPIC;

/**
 * Description: rocketmq配置，初始化消费者
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年06月30日   chenq	 Create
 * </pre>
 */
@Configuration
public class RocketMQConfiguration {

    @Value("${log.mq.namesrvAddr:}")
    private String namesrvAddr;

    @Value("${log.mq.messageBatchMaxSize:1000}")
    private String messageBatchMaxSize;

    @Value("${log.mq.consumerGroup:distributedlog_consumer_group}")
    private String consumerGroup;

    /**
     * 默认的队列消费者
     *
     * @param logService
     * @return
     * @throws Exception
     */
    @Bean
    public DefaultMQPushConsumer consumer(LogService logService) throws Exception {
        if (StringUtils.isBlank(namesrvAddr)) {
            return null;
        }
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setNamesrvAddr(namesrvAddr);
        consumer.setConsumerGroup(consumerGroup);
        consumer.setConsumeMessageBatchMaxSize(Integer.parseInt(messageBatchMaxSize));
        consumer.subscribe(ROCKETMQ_TOPIC, MessageSelector.byTag(ROCKETMQ_TAGS));
        consumer.registerMessageListener(new RocketMQLogConsumerListener(logService));
        return consumer;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public RocketMQConsumerLifecycle rocketmqConsumerLifecycle() {
        return new RocketMQConsumerLifecycle();
    }
}
