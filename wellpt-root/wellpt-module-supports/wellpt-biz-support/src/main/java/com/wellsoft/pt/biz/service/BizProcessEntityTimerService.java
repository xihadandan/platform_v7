/*
 * @(#)8/6/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.pt.biz.dao.BizProcessEntityTimerDao;
import com.wellsoft.pt.biz.entity.BizProcessEntityTimerEntity;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.timer.dto.TsTimerDto;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/6/24.1	    zhulh		8/6/24		    Create
 * </pre>
 * @date 8/6/24
 */
public interface BizProcessEntityTimerService extends JpaService<BizProcessEntityTimerEntity, BizProcessEntityTimerDao, Long> {

    /**
     * @param entityId
     * @param stateField
     * @param stateCode
     * @param configId
     * @param processInstUuid
     * @return
     */
    BizProcessEntityTimerEntity getNormalByEntityIdAndState(String entityId, String stateField, String stateCode,
                                                            String configId, String processInstUuid);

    /**
     * 更新计时器信息
     *
     * @param timerEntity
     * @param tsTimerDto
     */
    void updateTimerData(BizProcessEntityTimerEntity timerEntity, TsTimerDto tsTimerDto);

    /**
     * @param timerUuid
     * @return
     */
    BizProcessEntityTimerEntity getByTimerUuid(String timerUuid);
}
