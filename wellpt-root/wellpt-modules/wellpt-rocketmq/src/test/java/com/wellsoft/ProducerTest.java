package com.wellsoft;

import com.google.gson.Gson;
import com.wellsoft.context.util.NetUtils;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testng.collections.Maps;

import java.util.Map;

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
public class ProducerTest {

    DefaultMQProducer defaultMQProducer = null;

    @Before
    public void before() throws Exception {
        defaultMQProducer = new DefaultMQProducer("WellsoftTest");
        defaultMQProducer.setNamesrvAddr("192.168.0.116:19876");
        defaultMQProducer.start();
    }

    @Test
    public void testSend() throws Exception {
        for (int i = 0; i < 10; i++) {
            Message message = new Message();
            Map<String, String> data = Maps.newHashMap();
            data.put(i + "", i + "");
            message.setBody(new Gson().toJson(data).getBytes());
            message.setTopic("wellpt-topic");
            message.setKeys(i + "");
            message.setTags("Hello2");
            message.putUserProperty("ips", NetUtils.getLocalAddress());
            SendResult result = defaultMQProducer.send(message);
            Assert.assertTrue(SendStatus.SEND_OK.equals(result.getSendStatus()));
        }
    }


    @After
    public void after() throws Exception {
        defaultMQProducer.shutdown();
    }
}
