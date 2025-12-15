/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.timer.dao.TsWorkTimePlanHistoryDao;
import com.wellsoft.pt.timer.entity.TsWorkTimePlanHistoryEntity;

import java.util.Date;
import java.util.List;

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
public interface TsWorkTimePlanHistoryService
        extends JpaService<TsWorkTimePlanHistoryEntity, TsWorkTimePlanHistoryDao, String> {

    /**
     * @param workTimePlanUuid
     */
    void deleteByWorkTimePlanUuid(String workTimePlanUuid);

    /**
     * 获取最新激话的工作时间方案历史
     *
     * @param workTimePlanUuid
     * @return
     */
    TsWorkTimePlanHistoryEntity getActiveByWorkTimePlanUuid(String workTimePlanUuid);

    /**
     * 根据工作方案UUID列表获取工作时间方案历史
     *
     * @param uuids
     * @return
     */
    List<TsWorkTimePlanHistoryEntity> listByWorkTimePlanUuids(List<String> workTimePlanUuids);

    /**
     * 历史记录失效
     *
     * @param workTimePlanUuid
     * @param deactiveTime
     */
    void deactiveByWorkTimePlanUuid(String workTimePlanUuid, Date deactiveTime);

}
