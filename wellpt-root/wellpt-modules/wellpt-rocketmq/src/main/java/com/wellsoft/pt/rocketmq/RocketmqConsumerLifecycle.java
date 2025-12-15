package com.wellsoft.pt.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;

import java.util.List;

/**
 * Description: 消费者监听生命周期管理
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月28日   chenq	 Create
 * </pre>
 */
public class RocketmqConsumerLifecycle implements SmartLifecycle {

    @Autowired
    private List<DefaultMQPushConsumer> consumers;

    private volatile boolean isRunning = false;

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {

    }

    @Override
    public void start() {
        for (DefaultMQPushConsumer c : consumers) { // 启动
            try {
                c.start();
            } catch (Exception e) {
                throw new RuntimeException("启动消息队列消费者监听器异常", e);
            }

        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.stop(), "MQ ShutdownHook"));
        isRunning = true;
    }

    @Override
    public void stop() {
        if (isRunning) {
            for (DefaultMQPushConsumer c : consumers) {
                try {
                    c.shutdown();
                } catch (Exception e) {
                    throw new RuntimeException("停止消息队列消费者监听器异常", e);
                }

            }
            isRunning = false;
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
