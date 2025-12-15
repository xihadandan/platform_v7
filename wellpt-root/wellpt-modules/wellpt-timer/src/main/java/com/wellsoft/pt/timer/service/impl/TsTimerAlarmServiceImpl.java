/*
 * @(#)8/8/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.timer.dao.TsTimerAlarmDao;
import com.wellsoft.pt.timer.entity.TsTimerAlarmEntity;
import com.wellsoft.pt.timer.service.TsTimerAlarmService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/8/24.1	    zhulh		8/8/24		    Create
 * </pre>
 * @date 8/8/24
 */
@Service
public class TsTimerAlarmServiceImpl extends AbstractJpaServiceImpl<TsTimerAlarmEntity, TsTimerAlarmDao, Long>
        implements TsTimerAlarmService {

    @Override
    public List<TsTimerAlarmEntity> list2Alarm() {
        TsTimerAlarmEntity entity = new TsTimerAlarmEntity();
        entity.setAlarmDoingDone(false);
        entity.setDeleteStatus(0);
        return this.dao.listByEntity(entity);
    }

}
