package com.wellsoft.pt.message.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.message.dao.impl.ScheduleMessageQueueHisDaoImpl;
import com.wellsoft.pt.message.entity.ScheduleMessageQueueHisEntity;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/7/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/14    chenq		2018/7/14		Create
 * </pre>
 */
public interface ScheduleMessageQueueHisService extends
        JpaService<ScheduleMessageQueueHisEntity, ScheduleMessageQueueHisDaoImpl, String> {
    Long countAllHis();
}
