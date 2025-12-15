/*
 * @(#)2021-01-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.facade.service.AppModuleMgr;
import com.wellsoft.pt.log.LogEvent;
import com.wellsoft.pt.log.entity.BusinessOperationLog;
import com.wellsoft.pt.log.facade.service.LogFacadeService;
import com.wellsoft.pt.log.service.BusinessOperationLogService;
import com.wellsoft.pt.rocketmq.support.MqUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Description: 数据库表LOG_BUSINESS_OPERATION的门面服务实现类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-01-08.1	zhongzh		2021-01-08		Create
 * </pre>
 * @date 2021-01-08
 */
@Service
public class LogFacadeServiceImpl extends AbstractApiFacade implements LogFacadeService {

    @Autowired
    private AppModuleMgr appModuleMgr;

    @Resource(name = "transactionMQProducer")
    private MQProducer transactionMQProducer;

    private boolean logWithoutMqToConsole = Boolean.getBoolean("log.withoutMq.toConsole");

    @Autowired
    private BusinessOperationLogService logBusinessOperationService;

    @Override
    public String getModuleNameById(String id) {
        return appModuleMgr.getModuleNameById(id);
    }

    @Override
    public void sendLogEvent(LogEvent event) {
        sendLogEvent(LogEvent.TAGS_LOG, event);
    }

    public void sendLogEvent(String tags, LogEvent event) {
        sendLogEventInternal(false, tags, event);
    }

    @Override
    public void sendLogEventOutTransation(LogEvent event) {
        sendLogEventOutTransation(LogEvent.TAGS_LOG, event);
    }

    public void sendLogEventOutTransation(String tags, LogEvent event) {
        sendLogEventInternal(true, tags, event);
    }

    private void sendLogEventInternal(boolean withOutTransaction, String tags, LogEvent event) {
        Assert.notNull(event, "event is not null");
        BusinessOperationLog source = event.getSource();
        if (null == transactionMQProducer) {
            // logBusinessOperationService.save(source);
            if (logWithoutMqToConsole) {
                logger.info("not exists bean transactionMQProducer:" + JsonUtils.object2Json(source));
            }
            return;// 未启用MQ
        }
        Assert.notNull(tags, "tags is not null");
        // 保存用户信息和
        Date currentTime = new Date();
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        if (null == source.getUserId()) {
            source.setUserId(currentUserId);
        }
        if (null == source.getCreator()) {
            source.setCreator(currentUserId);
        }
        if (null == source.getModifier()) {
            source.setModifier(currentUserId);
        }
        if (null == source.getCreateTime()) {
            source.setCreateTime(currentTime);
        }
        if (null == source.getModifyTime()) {
            source.setModifyTime(currentTime);
        }
        if (null == source.getUserName()) {
            source.setUserName(SpringSecurityUtils.getCurrentUserName());
        }
        if (null == source.getSystemUnitId()) {
            source.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        }
        try {
            SendResult sendResult = null;
            Message msg = new Message(MqUtils.getTopic(LogFacadeService.TOPIC), tags.toLowerCase(), JsonUtils
                    .object2Gson(event).getBytes("UTF-8"));
            if (withOutTransaction) {
                sendResult = transactionMQProducer.send(msg);
            } else {
                sendResult = transactionMQProducer.sendMessageInTransaction(msg, null);
            }
            if (SendStatus.SEND_OK == sendResult.getSendStatus()) {
                // 发送成功
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }
}
