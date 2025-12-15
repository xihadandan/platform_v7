package com.wellsoft.pt.task.service;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.EntityService;
import com.wellsoft.pt.task.dao.impl.QrtzFiredTriggerHisDaoImpl;
import com.wellsoft.pt.task.entity.QrtzFiredTriggerHisEntity;

import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/7/12
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/7/12    chenq		2018/7/12		Create
 * </pre>
 */
public interface QrtzFiredTriggerHisService extends
        EntityService<QrtzFiredTriggerHisEntity, QrtzFiredTriggerHisDaoImpl> {

    List<QrtzFiredTriggerHisEntity> query(QueryInfo queryInfo);

    void deleteAll();

    void deleteByJobName(String jobName);
}
