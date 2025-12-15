package com.wellsoft.pt.message.service.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.message.dao.impl.ScheduleMessageQueueDaoImpl;
import com.wellsoft.pt.message.entity.ScheduleMessageQueueEntity;
import com.wellsoft.pt.message.entity.ScheduleMessageQueueHisEntity;
import com.wellsoft.pt.message.service.MessageQueueService;
import com.wellsoft.pt.message.service.ScheduleMessageQueueService;
import com.wellsoft.pt.message.support.JmsMessage;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.hibernate.LockOptions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.util.Date;
import java.util.List;

/**
 * Description: 定时消息队列服务
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
@Service
public class ScheduleMessageQueueServiceImpl extends
        AbstractJpaServiceImpl<ScheduleMessageQueueEntity, ScheduleMessageQueueDaoImpl, String> implements
        ScheduleMessageQueueService {

    @Autowired
    MessageQueueService messageQueueService;

    @Override
    @Transactional
    public void send(JmsMessage jmsMessage) {
        if (jmsMessage != null) {
            ScheduleMessageQueueEntity queueEntity = new ScheduleMessageQueueEntity();
            queueEntity.setTemplateId(jmsMessage.getTemplateId());
            queueEntity.setSendTime(jmsMessage.getSendTime());
            queueEntity.setName(jmsMessage.getName());
            queueEntity.setBusinessId(jmsMessage.getCorrelationId());
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo;
            try {
                oo = new ObjectOutputStream(bo);
                oo.writeObject(jmsMessage);
                byte[] bytes = bo.toByteArray();
                Blob blob = new SerialBlob(bytes);
                queueEntity.setMessage(blob);
                this.dao.save(queueEntity);
            } catch (Exception e) {
                logger.error("保存定时消息队列异常：", e);
            }
        }
    }

    @Override
    public List<ScheduleMessageQueueEntity> listBySendTimeLessThanEqual(Date date,
                                                                        PagingInfo pagingInfo) {


        return this.dao.listBySendTimeLessThanEqual(date, pagingInfo);
    }


    @Override
    public List<String> listUuidsBySendTimeLessThanEqual(Date date,
                                                         PagingInfo pagingInfo) {


        return this.dao.listUuidsBySendTimeLessThanEqual(date, pagingInfo);
    }


    @Override
    @Transactional
    public void saveQueue2His(ScheduleMessageQueueEntity queueEntity) {
        ScheduleMessageQueueHisEntity hisEntity = new ScheduleMessageQueueHisEntity();
        BeanUtils.copyProperties(queueEntity, hisEntity);
        this.dao.getSession().save(hisEntity);
    }


    @Override
    public List<ScheduleMessageQueueEntity> query(QueryInfo queryInfo) {
        List<ScheduleMessageQueueEntity> queues = this.dao.listByEntity(
                new ScheduleMessageQueueEntity(),
                queryInfo.getPropertyFilters(),
                queryInfo.getOrderBy(), queryInfo.getPagingInfo());
        return queues;
    }

    @Override
    @Transactional
    public boolean deleteByBusinessId(String businessId) {
        return this.dao.deleteByBusinessId(businessId);
    }


    @Override
    @Transactional
    public void sendScheduleMsg2RealTimeQueue(String uuid) {
        //发送时候进行更新锁，防止撤回同时发生
        ScheduleMessageQueueEntity entity = this.dao.getLockOne(
                uuid, LockOptions.UPGRADE);
        if (entity == null) {
            return;
        }
        try {
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, entity.getCreator());
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    entity.getMessage().getBinaryStream());
            //发送给即时消息队列
            messageQueueService.send((JmsMessage) objectInputStream.readObject());
            //备份到历史表
            this.saveQueue2His(entity);
            //删除定时队列
            this.delete(entity);
        } catch (Exception e) {
            logger.error("定时消息队列转即时消息队列异常：", e);
        } finally {
            IgnoreLoginUtils.logout();
        }

    }

    @Override
    public Long countAll() {
        return this.dao.countByEntity(new ScheduleMessageQueueEntity());
    }


}
