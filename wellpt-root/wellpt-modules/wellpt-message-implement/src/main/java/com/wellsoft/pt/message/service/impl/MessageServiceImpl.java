package com.wellsoft.pt.message.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.message.entity.MessageQueue;
import com.wellsoft.pt.message.processor.MessageProcessor;
import com.wellsoft.pt.message.processor.impl.*;
import com.wellsoft.pt.message.provider.MessageIntefaceSourceProvider;
import com.wellsoft.pt.message.service.MessageQueueHisService;
import com.wellsoft.pt.message.service.MessageQueueService;
import com.wellsoft.pt.message.service.MessageService;
import com.wellsoft.pt.message.support.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    private static Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired(required = false)
    private Map<String, MessageIntefaceSourceProvider> intefaceSourceProviderMap;
    @Autowired
    private MessageQueueService messageQueueService;
    @Autowired
    private MessageQueueHisService messageQueueHisService;

    @Override
    public List getIntefaceSourceList(String s, String id) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        TreeNode treeNode;
        for (String key : intefaceSourceProviderMap.keySet()) {
            MessageIntefaceSourceProvider source = intefaceSourceProviderMap.get(key);
            treeNode = new TreeNode();
            treeNode.setName(source.getIntefaceName());
            treeNode.setId(key);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    @Override
    @Transactional
    public void messageProcess(MessageQueue messageQueue) throws Exception {
        ObjectInputStream in = new ObjectInputStream(messageQueue.getMessage().getBinaryStream());
        Message message = (Message) in.readObject();
        in.close();
        message.setSystem(messageQueue.getSystem());
        message.setTenant(messageQueue.getTenant());
        receive(message);// 发送消息异常抛出，不修改迁移数据
        messageQueueHisService.saveByQueue(messageQueue);
        messageQueueService.delete(messageQueue);
        messageQueueService.flushSession();
    }

    public static synchronized void receive(Message message) throws Exception {
        try {
            String type = message.getType();
            if (Message.TYPE_ON_LINE.equals(type)) {
                ApplicationContextHolder.getBean(OnlineMessageProcessor.class).doProcessor(message);
            } else if (Message.TYPE_EMAIL.equals(type)) {
                ApplicationContextHolder.getBean("mailMessageProcessor", MessageProcessor.class).doProcessor(message);
            } else if (Message.TYPE_DINGTALK.equals(type)) {
                ApplicationContextHolder.getBean("dingtalkMessageProcessor", MessageProcessor.class).doProcessor(
                        message);
            } else if (Message.TYPE_SMS.equals(type)) {
                ApplicationContextHolder.getBean(SmsMessageProcessor.class).doProcessor(message);
            } else if (Message.TYPE_CANCEL.equals(type)) {
                ApplicationContextHolder.getBean(CancelMessageProcessor.class).doProcessor(message);
            } else if (Message.TYPE_WEB_SERVICE.equals(type)) {
                ApplicationContextHolder.getBean(WebServiceMessageProcessor.class).doProcessor(message);
            } else if (Message.TYPE_ONLINE_CANCEL.equals(type)) {
                ApplicationContextHolder.getBean(CancelOnlineMessageProcessor.class).doProcessor(message);
            } else if (Message.TYPE_INTEFACE.equals(type)) {
                ApplicationContextHolder.getBean(IntefaceMessageProcessor.class).doProcessor(message);
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
