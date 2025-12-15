package com.wellsoft.distributedlog.rocketmq;

import com.google.common.base.Throwables;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

/**
 * Description: mq工厂类，生成消费者
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年07月01日   chenq	 Create
 * </pre>
 */
public class DefaultMQFactory {
    private static DefaultMQProducer producer = null;

    public static DefaultMQProducer producer(String nameSrvAddr, String producerGroup) {
        if (producer == null) {
            synchronized (DefaultMQFactory.class) {
                if (producer == null) {
                    producer = new DefaultMQProducer();
                    producer.setNamesrvAddr(nameSrvAddr);
                    producer.setProducerGroup(producerGroup);
                    try {
                        producer.start();
                    } catch (Exception e) {
                        throw new RuntimeException("rocketmq生产者启动失败：" + Throwables.getStackTraceAsString(e));
                    }
                }
            }
        }
        return producer;
    }
}
