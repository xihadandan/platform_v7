/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.timer.dao.TsTimerLogDao;
import com.wellsoft.pt.timer.entity.TsTimerEntity;
import com.wellsoft.pt.timer.entity.TsTimerLogEntity;
import com.wellsoft.pt.timer.enums.EnumTimerLogType;
import com.wellsoft.pt.timer.service.TsTimerLogService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年4月7日.1	zhulh		2021年4月7日		Create
 * </pre>
 * @date 2021年4月7日
 */
@Service
public class TsTimerLogServiceImpl extends AbstractJpaServiceImpl<TsTimerLogEntity, TsTimerLogDao, String>
        implements TsTimerLogService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerLogService#log(com.wellsoft.pt.timer.entity.TsTimerEntity, com.wellsoft.pt.timer.enums.EnumTimerLogType)
     */
    @Override
    @Transactional
    public void log(TsTimerEntity timerEntity, EnumTimerLogType enumTimerLogType) {
        this.log(timerEntity, enumTimerLogType, StringUtils.EMPTY);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerLogService#log(com.wellsoft.pt.timer.entity.TsTimerEntity, com.wellsoft.pt.timer.enums.EnumTimerLogType, java.lang.String)
     */
    @Override
    @Transactional
    public void log(TsTimerEntity timerEntity, EnumTimerLogType enumTimerLogType, String remark) {
        TsTimerLogEntity timerLogEntity = new TsTimerLogEntity();
        timerLogEntity.setTimerUuid(timerEntity.getUuid());
        timerLogEntity.setLogTime(Calendar.getInstance().getTime());
        timerLogEntity.setType(enumTimerLogType.getValue());
        timerLogEntity.setTimeLimit(timerEntity.getTimeLimit());
        timerLogEntity.setDueTime(timerEntity.getDueTime());
        timerLogEntity.setRemark(remark);
        this.dao.save(timerLogEntity);
    }

    @Override
    public TsTimerLogEntity getByTimerUuidAndType(String timerUuid, EnumTimerLogType logType) {
        String hql = "from TsTimerLogEntity t where t.timerUuid = :timerUuid and t.type = :type order by t.createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("timerUuid", timerUuid);
        params.put("type", logType.getValue());
        List<TsTimerLogEntity> logEntities = this.dao.listByHQL(hql, params);
        if (CollectionUtils.isNotEmpty(logEntities)) {
            return logEntities.get(0);
        }
        return null;
    }

}
