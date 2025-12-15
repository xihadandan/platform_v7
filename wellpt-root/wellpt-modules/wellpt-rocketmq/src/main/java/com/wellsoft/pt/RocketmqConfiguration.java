package com.wellsoft.pt;

import com.wellsoft.context.config.Config;
import com.wellsoft.pt.rocketmq.RocketmqConsumerLifecycle;
import com.wellsoft.pt.rocketmq.RocketmqListenerAnnotationBeanPostProcessor;
import com.wellsoft.pt.rocketmq.RocketmqListenerAnnotationPostProcessor;
import com.wellsoft.pt.rocketmq.listeners.MqTransactionListener;
import com.wellsoft.pt.rocketmq.service.MqTransactionService;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

import java.util.concurrent.*;

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
@Configuration
public class RocketmqConfiguration {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public TransactionMQProducer transactionMQProducer(MqTransactionService mqTransactionService) {
        TransactionMQProducer producer = new TransactionMQProducer(Config.getValue("rocketmq.transactionProducerGroup",
                "defaultTransactionMQProducer"));
        int checkThreadCorePoolSize = Integer.parseInt(Config.getValue("rocketmq.checkThreadCorePoolSize", "5"));
        int checkThreadMaxPoolSize = Integer.parseInt(Config.getValue("rocketmq.checkThreadMaxPoolSize", "10"));
        int checkThreadKeepAliveTime = Integer.parseInt(Config.getValue("rocketmq.checkThreadKeepAliveTime", "60"));
        int checkThreadQueueSize = Integer.parseInt(Config.getValue("rocketmq.checkThreadQueueSize", "2000"));
        ExecutorService executorService = new ThreadPoolExecutor(checkThreadCorePoolSize, checkThreadMaxPoolSize,
                checkThreadKeepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(checkThreadQueueSize),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("client-transaction-msg-check-thread");
                        return thread;
                    }
                });
        // MqUtils.setNamespace(producer);
        producer.setNamesrvAddr(nameSrvAddr());
        producer.setExecutorService(executorService);
        producer.setTransactionListener(new MqTransactionListener(mqTransactionService));
        try {
            producer.start();
        } catch (Exception e) {
            logger.warn("rocketmq生产者启动失败：", e);
        }
        return producer;
    }

    private String nameSrvAddr() {
        String nameSrvAddr = Config.getValue("rocketmq.namesrvAddr");
        if (StringUtils.isBlank(nameSrvAddr) || (nameSrvAddr.startsWith("${") && nameSrvAddr.endsWith("}"))) {
            throw new RuntimeException("请配置rocketmq名字服务地址:rocketmq.namesrvAddr");
        }
        return nameSrvAddr;
    }

    /**
     * 默认的生产者
     *
     * @return
     */
    @Bean
    public DefaultMQProducer defaultMQProducer() {
        DefaultMQProducer producer = new DefaultMQProducer();
        // MqUtils.setNamespace(producer);
        producer.setNamesrvAddr(nameSrvAddr());
        producer.setProducerGroup(Config.getValue("rocketmq.producerGroup", "wellptProducerGroup"));
        try {
            producer.start();
        } catch (Exception e) {
            logger.warn("rocketmq生产者启动失败：", e);
        }
        return producer;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public RocketmqListenerAnnotationPostProcessor rocketmqListenerAnnotationPostProcessor() {
        return new RocketmqListenerAnnotationPostProcessor();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public RocketmqListenerAnnotationBeanPostProcessor rocketmqListenerAnnotationBeanPostProcessor() {
        return new RocketmqListenerAnnotationBeanPostProcessor();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public RocketmqConsumerLifecycle rocketmqConsumerLifecycle() {
        return new RocketmqConsumerLifecycle();
    }

}
