package com.wellsoft.pt.message.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.message.dao.impl.ScheduleMessageQueueDaoImpl;
import com.wellsoft.pt.message.entity.ScheduleMessageQueueEntity;
import com.wellsoft.pt.message.support.JmsMessage;

import java.util.Date;
import java.util.List;

/**
 * Description: 定时发送消息队列服务
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
public interface ScheduleMessageQueueService extends
        JpaService<ScheduleMessageQueueEntity, ScheduleMessageQueueDaoImpl, String> {

    /**
     * 发送，保存到定时消息队列
     *
     * @param jmsMessage
     */
    void send(JmsMessage jmsMessage);

    /**
     * 查询发送时间小于等于当前时间的所有定时发送消息队列数据
     *
     * @param date
     * @param pagingInfo
     * @return
     */
    List<ScheduleMessageQueueEntity> listBySendTimeLessThanEqual(Date date, PagingInfo pagingInfo);

    List<String> listUuidsBySendTimeLessThanEqual(Date date,
                                                  PagingInfo pagingInfo);

    void saveQueue2His(ScheduleMessageQueueEntity queueEntity);

    List<ScheduleMessageQueueEntity> query(QueryInfo queryInfo);

    /**
     * 删除定时消息队列
     *
     * @param businessId
     * @return
     */
    boolean deleteByBusinessId(String businessId);

    /**
     * 发送给即时消息队列
     *
     * @param uuid
     */
    void sendScheduleMsg2RealTimeQueue(String uuid);

    Long countAll();

}
