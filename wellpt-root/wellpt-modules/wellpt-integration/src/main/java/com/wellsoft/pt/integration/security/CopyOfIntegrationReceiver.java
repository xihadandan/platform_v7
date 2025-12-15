/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.security;

import com.wellsoft.pt.integration.support.WebServiceMessageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * Description: 数据交换平台的消息生产者(发送者)
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-25.1	Administrator		2013-11-25		Create
 * </pre>
 * @date 2013-11-25
 */
public class CopyOfIntegrationReceiver implements MessageListener {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onMessage(Message message) {
        //		logger.debug(message.toString());
        try {
            if (message instanceof ObjectMessage) {
                WebServiceMessageBean webServiceMessageBean = (WebServiceMessageBean) ((ObjectMessage) message)
                        .getObject();
                System.out.println(webServiceMessageBean.getWay());
                //接收处理消息
                //				ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                //						.getBean(ExchangeDataFlowService.class);
                //				if (webServiceMessage.getWay().equals("callbackClient")) {
                //					exchangeDataFlowService.callbackClient(webServiceMessage);
                //				} else if (webServiceMessage.getWay().equals("receiveClient")) {
                //					exchangeDataFlowService.receiveClient(webServiceMessage);
                //				} else if (webServiceMessage.getWay().equals("RouteCallBackClient")) {
                //					exchangeDataFlowService.RouteCallBackClient(webServiceMessage);
                //				} else if (webServiceMessage.getWay().equals("replyClient")) {
                //					exchangeDataFlowService.replyClient(webServiceMessage);
                //				}
            }
        } catch (JMSException e) {
            logger.info(e.getMessage());
        }
    }
}
