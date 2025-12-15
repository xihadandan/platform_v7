/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.security;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.integration.service.ExchangeDataFlowService;
import com.wellsoft.pt.integration.support.WebServiceMessage;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class IntegrationReceiver implements MessageListener {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                WebServiceMessage webServiceMessage = (WebServiceMessage) ((ObjectMessage) message).getObject();
                System.out.println("接收消息：" + webServiceMessage.getWay());
                if (!StringUtils.isBlank(webServiceMessage.getUserId())) {
                    IgnoreLoginUtils.login(webServiceMessage.getTenantId(), webServiceMessage.getUserId());
                } else {
                    IgnoreLoginUtils.login(webServiceMessage.getTenantId(), webServiceMessage.getTenantId());
                }
                // 接收处理消息
                ExchangeDataFlowService exchangeDataFlowService = ApplicationContextHolder
                        .getBean(ExchangeDataFlowService.class);
                if (webServiceMessage.getWay().equals("callbackClientAndreceiveClient")) {
                    exchangeDataFlowService.callbackClientAndreceiveClient(webServiceMessage);
                } else if (webServiceMessage.getWay().equals("RouteCallBackClient")) {
                    exchangeDataFlowService.RouteCallBackClient(webServiceMessage);
                } else if (webServiceMessage.getWay().equals("replyClient")) {
                    exchangeDataFlowService.replyClient(webServiceMessage);
                } else if (webServiceMessage.getWay().equals("receiveClient")) {
                    exchangeDataFlowService.receiveClient(webServiceMessage);
                } else if (webServiceMessage.getWay().equals("cancelClient")) {
                    exchangeDataFlowService.cancelClient(webServiceMessage);
                } else if (webServiceMessage.getWay().equals("dxSendAsynchronous")) {
                    exchangeDataFlowService.dxSendAsynchronous(webServiceMessage, webServiceMessage.getSource());
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            logger.error(e.toString());
            throw new RuntimeException(e);
        } finally {
            IgnoreLoginUtils.logout();
        }
    }
}
