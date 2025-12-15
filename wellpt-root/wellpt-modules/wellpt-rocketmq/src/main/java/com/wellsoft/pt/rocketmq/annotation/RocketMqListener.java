package com.wellsoft.pt.rocketmq.annotation;

import org.apache.rocketmq.common.consumer.ConsumeFromWhere;

import java.io.Serializable;
import java.lang.annotation.*;

/**
 * Description: 消费者监听构建声明
 * <p>
 * 框架会解析注解@{@link org.springframework.stereotype.Component}的类中注解@{@link RocketMqListener}的方法，自动注册为消费者侦听。
 *
 * @{@link RocketMqListener}的requireNew等于false，则会把方法加入默认的消费者监听进行处理，否则会新建消费者监听，即该方法独享一个消费者监听，
 * 此时需要命名消费者群组名consumerGroupName，否则会按照{类名_方法名}的格式创建消费者群组名，重复则命名追加随机数
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月28日   chenq	 Create
 * </pre>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RocketMqListener {

    /**
     * 订阅的话题，仅 requireNew = true时有效
     *
     * @return
     */
    String topic() default "${rocketmq.topic}";

    /**
     * 订阅的tags
     *
     * @return
     */
    String tags() default "";

    /**
     * 是否新建消费者
     *
     * @return
     */
    boolean requireNew() default false;

    /**
     * 消息内容转换类
     *
     * @return
     */
    Class<? extends Serializable> body() default String.class;

    /**
     * 批量处理消息条数，仅 requireNew = true时有效
     *
     * @return
     */
    int consumeMessageBatchMaxSize() default 1;

    /**
     * 可以根据发送时候设置的消息属性进行SQL语法的筛选，仅 requireNew = true时有效，该属性与tags是互斥的，只能任选一个
     *
     * @return
     */
    String messageSelectorBySQL() default "";

    /**
     * 仅 requireNew = true时有效，定义消费群组名称。
     * 需要注意的: 一个JVM进程启动一个消费者群组，
     * 也就是说如果多个方法requireNew=true，且consumerGroupName都相同的情况下，是无法启动的，需要命名不同的consumerGroupName。
     * 默认情况下当requireNew=true, 且consumerGroupName无命名的情况下，会按照类名_方法名的方式生成消费者群组
     *
     * @return
     */
    String consumerGroupName() default "";

    ConsumeFromWhere consumeFromWhere() default ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET;
}
