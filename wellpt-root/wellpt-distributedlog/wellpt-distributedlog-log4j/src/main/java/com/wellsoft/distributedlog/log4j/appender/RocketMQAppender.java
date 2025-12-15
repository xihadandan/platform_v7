package com.wellsoft.distributedlog.log4j.appender;

import com.google.common.base.Throwables;
import com.wellsoft.distributedlog.Constants;
import com.wellsoft.distributedlog.rocketmq.DefaultMQFactory;
import org.apache.rocketmq.common.message.Message;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年06月29日   chenq	 Create
 * </pre>
 */
public class RocketMQAppender extends AbstractLogQueueAppender {


    private String namesrvAddr;

    private String producerGroup = Constants.ROCKETMQ_PRODUCER_GROUP;


    @Override
    protected void subAppend(List<String> logs) {
        Message message = new Message();
        message.setTopic(Constants.ROCKETMQ_TOPIC);
        message.setTags(Constants.ROCKETMQ_TAGS);
        for (String str : logs) {
            message.setBody(str.getBytes());
            try {
                DefaultMQFactory.producer(namesrvAddr, producerGroup).sendOneway(message);
            } catch (Exception e) {
                throw new RuntimeException("发送消息到rocketmq异常：" + Throwables.getStackTraceAsString(e));
            }
        }
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }
}
