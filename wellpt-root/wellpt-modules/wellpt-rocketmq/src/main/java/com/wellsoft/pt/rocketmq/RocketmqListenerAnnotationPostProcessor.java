package com.wellsoft.pt.rocketmq;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.pt.rocketmq.annotation.RocketMqListener;
import com.wellsoft.pt.rocketmq.support.MqUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Description: 构建生成{@link DefaultMQPushConsumer}实例
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月28日   chenq	 Create
 * </pre>
 */
public class RocketmqListenerAnnotationPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        String[] names = registry.getBeanDefinitionNames();
        Set<String> defaultConsumerTags = Sets.newHashSet();
        Map<String, Method> defaultTagsMethods = Maps.newHashMap();
        Map<String, String> defaultTagResolveBeans = Maps.newHashMap();
        Map<String, RocketMqListener> defaultTagMethodRocketListeners = Maps.newHashMap();
        BeanDefinition defaultConsumer = new RootBeanDefinition(DefaultMQPushConsumer.class);
        defaultConsumer.getPropertyValues().addPropertyValue("tagsBeans", defaultTagResolveBeans);
        defaultConsumer.getPropertyValues().addPropertyValue("tagsListeners", defaultTagMethodRocketListeners);
        defaultConsumer.getPropertyValues().add("tagsMethods", defaultTagsMethods);
        defaultConsumer.getPropertyValues().add("topic",
                AnnotationUtils.getDefaultValue(RocketMqListener.class, "topic").toString());// 默认的消费者
        for (String name : names) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(name);
            Class clazz = getBeanClass(beanDefinition, registry);
            if (clazz != null) {
                Map<Method, RocketMqListener> methods = MethodIntrospector.selectMethods(clazz,
                        new MethodIntrospector.MetadataLookup<RocketMqListener>() {
                            @Override
                            public RocketMqListener inspect(Method method) {
                                return method.getAnnotation(RocketMqListener.class);
                            }
                        });
                if (MapUtils.isNotEmpty(methods)) {
                    Set<Method> methodSet = methods.keySet();
                    for (Method m : methodSet) {
                        RocketMqListener rocketMqListener = methods.get(m);
                        String tags = rocketMqListener.tags().toLowerCase();
                        if (rocketMqListener.requireNew()) {
                            // 新建消费者
                            BeanDefinition mqBeanDefinition = new RootBeanDefinition(DefaultMQPushConsumer.class);
                            mqBeanDefinition.getPropertyValues().addPropertyValue("tagsBeans", name);
                            mqBeanDefinition.getPropertyValues().addPropertyValue("tagsListeners", rocketMqListener);
                            if (StringUtils.isBlank(rocketMqListener.tags())
                                    && StringUtils.isBlank(rocketMqListener.messageSelectorBySQL())) {
                                throw new RuntimeException("消费监听器必须定义tags、messageSelectorBySQL其中任一项");
                            }
                            if (StringUtils.isNotBlank(rocketMqListener.messageSelectorBySQL())) {
                                mqBeanDefinition.getPropertyValues().addPropertyValue("tags",
                                        MessageSelector.bySql(rocketMqListener.messageSelectorBySQL()));
                            } else if (StringUtils.isNotBlank(rocketMqListener.tags())) {
                                mqBeanDefinition.getPropertyValues().addPropertyValue("tags",
                                        MessageSelector.byTag(tags));
                            }
                            mqBeanDefinition.getPropertyValues().addPropertyValue("topic", MqUtils.getTopic(rocketMqListener.topic()));
                            mqBeanDefinition.getPropertyValues().addPropertyValue("tagsMethods", m);
                            String beanName = clazz.getSimpleName() + "_" + m.getName();
                            if (registry.isBeanNameInUse(beanName)) {
                                beanName += "_" + RandomStringUtils.randomNumeric(4);
                            }
                            registry.registerBeanDefinition(beanName, mqBeanDefinition);

                        } else {
                            // 添加到默认的消费者
                            boolean notDumplicate = defaultConsumerTags.add(tags);
                            if (!notDumplicate) {
                                logger.error("class = {} , method = {} , 重复的消费者tags = {}",
                                        new Object[]{clazz, m.toString(), rocketMqListener.tags()});
                                throw new BeanCreationException("创建rocketmqListener异常");
                            }
                            if (StringUtils.isBlank(rocketMqListener.tags())) {
                                throw new RuntimeException("消费监听器未定义tags");
                            }
                            defaultTagsMethods.put(tags, m);
                            defaultTagResolveBeans.put(tags, name);
                            defaultTagMethodRocketListeners.put(tags, rocketMqListener);
                        }
                    }
                }
            }
        }

        if (!defaultConsumerTags.isEmpty()) {
            defaultConsumer.getPropertyValues().addPropertyValue("tags",
                    MessageSelector.byTag(StringUtils.join(defaultConsumerTags, "||")));
            registry.registerBeanDefinition("defaultMQPushConsumer", defaultConsumer);
        }
    }

    private Class getBeanClass(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) {
        try {
            return ClassUtils.getClass(beanDefinition.getBeanClassName(), false);
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
