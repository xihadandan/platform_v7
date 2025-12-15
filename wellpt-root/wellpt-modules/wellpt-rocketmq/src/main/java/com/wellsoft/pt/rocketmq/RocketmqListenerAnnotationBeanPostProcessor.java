package com.wellsoft.pt.rocketmq;

import com.google.common.collect.Sets;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.rocketmq.annotation.RocketMqListener;
import com.wellsoft.pt.rocketmq.support.MqUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 构建生成{@link DefaultMQPushConsumer}实例进行注册/订阅消息处理逻辑，有两类消费者实例：
 * 1、默认消费者实例，负责监听所有tags
 * 2、按方法生成的消费者实例
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月28日   chenq	 Create
 * </pre>
 */
public class RocketmqListenerAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ConfigurableBeanFactory beanFactory;

    @Value("${rocketmq.namesrvAddr}")
    private String namesrvAddr;
    @Value("${rocketmq.consumerGroup:wellptConsumerGroup}")
    private String consumerGroup;

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean,
                                                    String beanName) throws BeansException {
        if (bean instanceof DefaultMQPushConsumer) {
            Set<String> conficts = Sets.newHashSet();
            DefaultMQPushConsumer consumer = (DefaultMQPushConsumer) bean;
            String topic = (String) pvs.getPropertyValue("topic").getValue();
            final MessageSelector tags = (MessageSelector) pvs.getPropertyValue("tags").getValue();
            boolean isDefaultMQPushConsumer = "defaultMQPushConsumer".equals(beanName);
            try {
                if (isDefaultMQPushConsumer) { // 默认的消费者监听器
                    consumer.setConsumeMessageBatchMaxSize(1);// 多个tag消费的情况，每个批次只会处理一个
                    consumer.setConsumerGroup(consumerGroup);
                    consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
                } else {
                    RocketMqListener listener = (RocketMqListener) (pvs.getPropertyValue("tagsListeners").getValue());
                    consumer.setConsumerGroup(MqUtils.getConsumerGroup(StringUtils.defaultIfBlank(listener.consumerGroupName(), beanName)));
                    consumer.setConsumeMessageBatchMaxSize(listener.consumeMessageBatchMaxSize());
                    consumer.setConsumeFromWhere(listener.consumeFromWhere());
                }
                // MqUtils.setNamespace(consumer);
                if (conficts.add(topic + tags.getExpression())) {
                    consumer.subscribe(topic, tags);
                } else {
                    throw new RuntimeException("重复订阅topic = [" + topic + "] , messageSelector = ["
                            + tags.getExpression() + "]");
                }
                consumer.setNamesrvAddr(namesrvAddr);
            } catch (Exception e) {
                logger.error("注册消费者异常：", e);
            }
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                                ConsumeConcurrentlyContext context) {
                    Object invoker = null;
                    Method method = null;
                    RocketMqListener rocketMqListener = null;
                    String t = msgs.get(0).getTags().toLowerCase();
                    if (!isDefaultMQPushConsumer) {
                        invoker = beanFactory.getBean(pvs.getPropertyValue("tagsBeans").getValue().toString());
                        rocketMqListener = (RocketMqListener) pvs.getPropertyValue("tagsListeners").getValue();
                        method = (Method) pvs.getPropertyValue("tagsMethods").getValue();
                    } else {
                        Map<String, String> tagsBeans = (Map<String, String>) pvs.getPropertyValue("tagsBeans")
                                .getValue();
                        Map<String, RocketMqListener> tagsListeners = (Map<String, RocketMqListener>) pvs
                                .getPropertyValue("tagsListeners").getValue();
                        Map<String, Method> tagsMethods = (Map<String, Method>) pvs.getPropertyValue("tagsMethods")
                                .getValue();
                        invoker = beanFactory.getBean(tagsBeans.get(t));
                        rocketMqListener = tagsListeners.get(t);
                        method = tagsMethods.get(t);
                    }

                    if (invoker == null || method == null || rocketMqListener == null) {
                        logger.warn("无定义的消费监听类进行消费");
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                    Object param = null;
                    try {
                        Class[] parameterTyps = method.getParameterTypes();
                        if (rocketMqListener.consumeMessageBatchMaxSize() > 1) {// 允许批量处理的情况
                            if (List.class.isAssignableFrom(parameterTyps[0])) {
                                param = Lists.newArrayList();
                            } else if (parameterTyps[0].isArray()) {
                                param = Array.newInstance(rocketMqListener.body(), msgs.size());
                            } else if (Set.class.isAssignableFrom(parameterTyps[0])) {
                                param = Sets.newHashSet();
                            }
                            for (int i = 0, len = msgs.size(); i < len; i++) {
                                String str = new String(msgs.get(i).getBody(), "UTF-8");
                                Object data = String.class.isAssignableFrom(rocketMqListener.body()) ? str : JsonUtils
                                        .gson2Object(str, rocketMqListener.body());
                                if (parameterTyps[0].isArray()) {
                                    Array.set(param, i, data);
                                } else {
                                    CollectionUtils.addAll((Collection) param, data);
                                }
                            }
                        } else {
                            String str = new String(msgs.get(0).getBody(), "UTF-8");
                            param = String.class.isAssignableFrom(rocketMqListener.body()) ? str : JsonUtils
                                    .gson2Object(str, rocketMqListener.body());
                        }
                    } catch (Exception e) {
                        logger.error("无法转换的报文:", e);
                        throw new RuntimeException(e.getMessage());
                    }
                    try {
                        ConsumeConcurrentlyStatus status = (ConsumeConcurrentlyStatus) method.invoke(invoker, param);
                        if (status != null) {
                            return status;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage());
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            return null;
        }

        return super.postProcessPropertyValues(pvs, pds, bean, beanName);

    }

}
