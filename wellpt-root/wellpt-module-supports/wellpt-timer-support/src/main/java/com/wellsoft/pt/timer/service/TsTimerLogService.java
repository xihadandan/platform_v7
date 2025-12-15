/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.timer.dao.TsTimerLogDao;
import com.wellsoft.pt.timer.entity.TsTimerEntity;
import com.wellsoft.pt.timer.entity.TsTimerLogEntity;
import com.wellsoft.pt.timer.enums.EnumTimerLogType;

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
public interface TsTimerLogService extends JpaService<TsTimerLogEntity, TsTimerLogDao, String> {

    /**
     * 记录日志
     *
     * @param timerEntity
     * @param enumTimerLogType
     */
    void log(TsTimerEntity timerEntity, EnumTimerLogType enumTimerLogType);

    /**
     * 记录日志
     *
     * @param timerEntity
     * @param enumTimerLogType
     * @param remark
     */
    void log(TsTimerEntity timerEntity, EnumTimerLogType enumTimerLogType, String remark);

    /**
     * @param timerUuid
     * @param logType
     * @return
     */
    TsTimerLogEntity getByTimerUuidAndType(String timerUuid, EnumTimerLogType logType);
}
