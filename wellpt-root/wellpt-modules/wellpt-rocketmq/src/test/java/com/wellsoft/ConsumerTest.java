package com.wellsoft;

import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.TopicMessageQueueChangeListener;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.junit.Before;

import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月28日   chenq	 Create
 * </pre>
 */
public class ConsumerTest {

    DefaultMQPushConsumer pushConsumer = null;

    DefaultLitePullConsumer pullConsumer = null;

    @Before
    public void before() throws Exception {
        pushConsumer = new DefaultMQPushConsumer("WellsoftTest");
        pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        pushConsumer.setNamesrvAddr("192.168.0.116:19876");
        pushConsumer.subscribe("Test", "");
        pushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//                msgs.get(0).getBody() -- > java bean
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        pullConsumer.registerTopicMessageQueueChangeListener("Test", new TopicMessageQueueChangeListener() {
            @Override
            public void onChanged(String topic, Set<MessageQueue> messageQueues) {
            }
        });
    }
}
