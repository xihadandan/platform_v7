package com.wellsoft.pt.rocketmq.producer;

import com.wellsoft.context.util.ApplicationContextHolder;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月29日   chenq	 Create
 * </pre>
 */
public class Producer {

    /**
     * 默认的消息生产者
     *
     * @return
     */
    public static DefaultMQProducer producer() {
        return ApplicationContextHolder.getBean("defaultMQProducer", DefaultMQProducer.class);
    }

    /**
     * 默认的事务消息生产者
     *
     * @return
     */
    public static TransactionMQProducer transactionProducer() {
        return ApplicationContextHolder.getBean("transactionMQProducer", TransactionMQProducer.class);
    }
}
