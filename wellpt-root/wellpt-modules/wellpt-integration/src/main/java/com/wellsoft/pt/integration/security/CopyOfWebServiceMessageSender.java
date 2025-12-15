/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.security;

import com.wellsoft.pt.integration.request.SendRequest;
import com.wellsoft.pt.integration.support.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSession;
import org.apache.activemq.BlobMessage;
import org.apache.activemq.ScheduledMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 消息接收处理类
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
public class CopyOfWebServiceMessageSender {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private JmsTemplate jmsTemplate;
    private Destination destination;

    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public void send(WebServiceMessage msg) throws IOException {
        WebServiceRequestBean webServiceRequestBean = new WebServiceRequestBean();
        SendRequest request = new SendRequest();//msg.getWebServiceRequest();
        webServiceRequestBean.setBatchId(request.getBatchId());
        webServiceRequestBean.setBcc(request.getBcc());
        webServiceRequestBean.setCc(request.getCc());
        webServiceRequestBean.setFrom(request.getFrom());
        webServiceRequestBean.setTo(request.getTo());
        webServiceRequestBean.setTypeId(request.getTypeId());
        List<DataItem> daItems = request.getDataList();
        List<DataItemBean> dataItemBeans = new ArrayList<DataItemBean>();
        for (DataItem dataItem : daItems) {

            DataItemBean dataItemBean = new DataItemBean();
            dataItemBean.setCorrelationId(dataItem.getCorrelationId());
            dataItemBean.setCorrelationRecVer(dataItem.getCorrelationRecVer());
            dataItemBean.setDataId(dataItem.getDataId());
            dataItemBean.setRecVer(dataItem.getRecVer());
            dataItemBean.setText(dataItem.getText());

            List<BlobMessage> blobMessages = new ArrayList<BlobMessage>();
            List<StreamingData> streamingDatas = dataItem.getStreamingDatas();
            for (StreamingData streamingData : streamingDatas) {
                ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
                Connection connection;
                try {
                    connection = connectionFactory.createConnection();
                    connection.start();
                    ActiveMQSession session = (ActiveMQSession) connection.createSession(Boolean.TRUE,
                            Session.AUTO_ACKNOWLEDGE);
                    Destination destination = session.createQueue("wellpt-queue3");
                    BlobMessage blobMessage = session
                            .createBlobMessage(streamingData.getDataHandler().getInputStream());
                    blobMessage.setStringProperty("fileName", streamingData.getFileName());
                    blobMessages.add(blobMessage);
                } catch (JMSException e) {
                    // TODO Auto-generated catch block
                    logger.info(e.getMessage());
                }
            }
            dataItemBean.setBlobMessages(blobMessages);

            dataItemBeans.add(dataItemBean);
        }
        webServiceRequestBean.setDataList(dataItemBeans);

        WebServiceMessageBean wmb = new WebServiceMessageBean();
        wmb.setWebServiceRequestBean(webServiceRequestBean);
        wmb.setBatchId(msg.getBatchId());
        wmb.setCode(msg.getCode());
        wmb.setDataId(msg.getDataId());
        wmb.setDate(msg.getDate());
        wmb.setDelay(msg.getDelay());
        wmb.setMsg(msg.getMsg());
        wmb.setRecVer(msg.getRecVer());
        wmb.setTypeId(msg.getTypeId());
        wmb.setUnitId(msg.getUnitId());
        wmb.setWay(msg.getWay());
        WebServiceMessageCreator mc = new WebServiceMessageCreator(wmb);
        jmsTemplate.send(destination, mc);
    }

    /**
     * Description: 创建JmsMessageCreator
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
    private static final class WebServiceMessageCreator implements MessageCreator {
        /**
         * JMS要发送的消息
         */
        private WebServiceMessageBean webServiceMessageBean;

        public WebServiceMessageCreator(WebServiceMessageBean webServiceMessageBean) {
            super();
            this.webServiceMessageBean = webServiceMessageBean;
        }

        /**
         * (non-Javadoc)
         *
         * @see org.springframework.jms.core.MessageCreator#createMessage(javax.jms.Session)
         */
        @Override
        public javax.jms.Message createMessage(Session session) throws JMSException {
            ObjectMessage message = session.createObjectMessage();
            message.setObject(webServiceMessageBean);
            if (webServiceMessageBean.getDelay() > 0) {
                message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, webServiceMessageBean.getDelay());
            }
            return message;
        }

    }
}
