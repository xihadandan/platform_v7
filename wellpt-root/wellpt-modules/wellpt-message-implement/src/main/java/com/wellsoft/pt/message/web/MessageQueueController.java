package com.wellsoft.pt.message.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.message.entity.MessageQueue;
import com.wellsoft.pt.message.entity.MessageQueueHis;
import com.wellsoft.pt.message.server.impl.JmsMessageConsumer;
import com.wellsoft.pt.message.service.MessageQueueHisService;
import com.wellsoft.pt.message.service.MessageQueueService;
import com.wellsoft.pt.message.service.ScheduleMessageQueueHisService;
import com.wellsoft.pt.message.service.ScheduleMessageQueueService;
import com.wellsoft.pt.message.service.impl.MessageServiceImpl;
import com.wellsoft.pt.message.support.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;

/**
 * Description: 消息队列控制层
 *
 * @author chenq
 * @date 2018/7/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/13    chenq		2018/7/13		Create
 * </pre>
 */
@Controller
@RequestMapping("/message/queue")
public class MessageQueueController extends BaseController {

    @Autowired
    MessageQueueService messageQueueService;

    @Autowired
    MessageQueueHisService messageQueueHisService;

    @Autowired
    ScheduleMessageQueueService scheduleMessageQueueService;

    @Autowired
    ScheduleMessageQueueHisService scheduleMessageQueueHisService;

    @RequestMapping(value = "/waitQueue")
    public String waitQueue() {
        return forward("/message/wait_queue");
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public @ResponseBody
    ResultMessage queueCount() {
        ResultMessage resultMessage = new ResultMessage();
        List<Map<String, Object>> queueCntList = Lists.newArrayList();

        Map<String, Object> mq = Maps.newHashMap();
        mq.put("value", messageQueueService.countAll());
        mq.put("name", "待处理即时消息队列");
        mq.put("code", "messageQueue");
        queueCntList.add(mq);

        Map<String, Object> mq2 = Maps.newHashMap();
        mq2.put("value", messageQueueHisService.countAllHis());
        mq2.put("name", "已处理即时消息队列");
        mq2.put("code", "messageQueueHis");
        queueCntList.add(mq2);

        Map<String, Object> mq3 = Maps.newHashMap();
        mq3.put("value", scheduleMessageQueueService.countAll());
        mq3.put("name", "待处理定时消息队列");
        mq3.put("code", "scheduleMessageQueueEntity");
        queueCntList.add(mq3);

        Map<String, Object> mq4 = Maps.newHashMap();
        mq4.put("value", scheduleMessageQueueHisService.countAllHis());
        mq4.put("name", "已处理定时消息队列");
        mq4.put("code", "scheduleMessageQueueHisEntity");
        queueCntList.add(mq4);
        resultMessage.setData(queueCntList);
        return resultMessage;
    }

    @RequestMapping(value = "/executeMessageQueueJob", method = RequestMethod.GET)
    public @ResponseBody
    ResultMessage executeMessageQueueJob() {
        JmsMessageConsumer jmsMessageConsumer = ApplicationContextHolder.getBean(JmsMessageConsumer.class);
        jmsMessageConsumer.receiveMessage();
        return new ResultMessage();
    }


    @RequestMapping(value = "/testSmsMessageProcessor", method = RequestMethod.GET)
    public @ResponseBody
    ResultMessage testSmsMessageProcessor() {
        try {
            MessageQueueHis his = messageQueueHisService.getOne("219419817248030720");
            ObjectInputStream in = new ObjectInputStream(his.getMessage().getBinaryStream());
            Message message = (Message) in.readObject();
            in.close();
            message.setSystem(his.getSystem());
            message.setTenant(his.getTenant());
            MessageServiceImpl.receive(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new ResultMessage();
    }


    @RequestMapping(value = "/resendMessage", method = RequestMethod.GET)
    public @ResponseBody
    ResultMessage resendMessage(@RequestParam String uuid) {
        try {
            MessageQueueHis his = messageQueueHisService.getOne(uuid);
            Message message = null;
            ObjectInputStream in = null;
            if (his == null) {
                MessageQueue queue = messageQueueService.getOne(uuid);
                in = new ObjectInputStream(queue.getMessage().getBinaryStream());
                message = (Message) in.readObject();
                message.setSystem(queue.getSystem());
                message.setTenant(queue.getTenant());

            } else {
                in = new ObjectInputStream(his.getMessage().getBinaryStream());
                message = (Message) in.readObject();
                message.setSystem(his.getSystem());
                message.setTenant(his.getTenant());
            }
            in.close();
            MessageServiceImpl.receive(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new ResultMessage();
    }
}
