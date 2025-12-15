package com.wellsoft.pt.message.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.message.dao.impl.ScheduleMessageQueueHisDaoImpl;
import com.wellsoft.pt.message.entity.ScheduleMessageQueueHisEntity;
import com.wellsoft.pt.message.service.ScheduleMessageQueueHisService;
import org.springframework.stereotype.Service;

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
@Service
public class ScheduleMessageQueueHisServiceImpl extends
        AbstractJpaServiceImpl<ScheduleMessageQueueHisEntity, ScheduleMessageQueueHisDaoImpl, String> implements
        ScheduleMessageQueueHisService {
    @Override
    public Long countAllHis() {
        return this.dao.countByEntity(new ScheduleMessageQueueHisEntity());
    }
}
