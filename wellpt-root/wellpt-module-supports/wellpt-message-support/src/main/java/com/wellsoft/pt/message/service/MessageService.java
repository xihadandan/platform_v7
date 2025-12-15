package com.wellsoft.pt.message.service;

import com.wellsoft.pt.message.entity.MessageQueue;

import java.util.List;

public interface MessageService {

    public List getIntefaceSourceList(String s, String id);

    /**
     * 消息队列处理，生成对应的消息发送
     *
     * @param messageQueue
     * @throws Exception
     */
    public void messageProcess(MessageQueue messageQueue) throws Exception;

}
