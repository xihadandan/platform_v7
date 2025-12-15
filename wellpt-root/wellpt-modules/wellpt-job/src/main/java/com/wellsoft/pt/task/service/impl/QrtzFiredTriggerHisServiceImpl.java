package com.wellsoft.pt.task.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractEntityServiceImpl;
import com.wellsoft.pt.task.dao.impl.QrtzFiredTriggerHisDaoImpl;
import com.wellsoft.pt.task.entity.QrtzFiredTriggerHisEntity;
import com.wellsoft.pt.task.service.QrtzFiredTriggerHisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
@Service
public class QrtzFiredTriggerHisServiceImpl extends
        AbstractEntityServiceImpl<QrtzFiredTriggerHisEntity, QrtzFiredTriggerHisDaoImpl> implements
        QrtzFiredTriggerHisService {

    @Override
    public List<QrtzFiredTriggerHisEntity> query(QueryInfo queryInfo) {
        List<QrtzFiredTriggerHisEntity> hisEntityList = this.dao.listByEntity(
                new QrtzFiredTriggerHisEntity(),
                queryInfo.getPropertyFilters(),
                queryInfo.getOrderBy(), queryInfo.getPagingInfo());
        return hisEntityList;
    }

    @Override
    @Transactional
    public void deleteAll() {
        this.dao.deleteBySQL("delete from QRTZ_FIRED_TRIGGERS_HIS", null);
    }

    @Override
    @Transactional
    public void deleteByJobName(String jobName) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("jobName", jobName);
        this.dao.deleteBySQL("delete from QRTZ_FIRED_TRIGGERS_HIS where job_name=:jobName", params);
    }
}
