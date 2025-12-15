package com.wellsoft.pt.message.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.message.entity.ScheduleMessageQueueEntity;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description:定时消息队列DAO
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
@Repository
public class ScheduleMessageQueueDaoImpl extends
        AbstractJpaDaoImpl<ScheduleMessageQueueEntity, String> {
    public List<ScheduleMessageQueueEntity> listBySendTimeLessThanEqual(Date date,
                                                                        PagingInfo pagingInfo) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("time", date);
        return this.listByHQLAndPage("from ScheduleMessageQueueEntity where sendTime<=:time", param,
                pagingInfo);

    }

    public boolean deleteByBusinessId(String businessId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("businessId", businessId);
        return this.deleteByHQL(
                "delete from ScheduleMessageQueueEntity where businessId=:businessId", param) > 0;
    }

    public List<String> listUuidsBySendTimeLessThanEqual(Date date, PagingInfo pagingInfo) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("time", date);
        return this.listCharSequenceByHqlAndPage(
                "select uuid from ScheduleMessageQueueEntity where sendTime<=:time", param,
                pagingInfo);
    }
}
