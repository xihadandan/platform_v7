/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.timer.dao.TsWorkTimePlanDao;
import com.wellsoft.pt.timer.entity.TsWorkTimePlanEntity;
import com.wellsoft.pt.timer.support.TsHolidayUsedChecker;

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
public interface TsWorkTimePlanService
        extends JpaService<TsWorkTimePlanEntity, TsWorkTimePlanDao, String>, TsHolidayUsedChecker {

    /**
     * @return
     */
    Date getSysDate();

    /**
     * @param name
     * @param systemUnitIds
     * @return
     */
    List<TsWorkTimePlanEntity> getAllBySystemUnitIdsLikeName(List<String> systemUnitIds, String name);

    /**
     * @param uuid
     * @param systemUnitId
     */
    void setAsDefaultByUuidAndSystemUnitId(String uuid, String systemUnitId);

    /**
     * @param uuid
     * @param isDefault
     */
    void setIsDefaultByUuid(String uuid, boolean isDefault);

    /**
     * 同步工作时间方案状态
     */
    void syncWorkTimePlanStatus();

    /**
     * @return
     */
    TsWorkTimePlanEntity getDefaultWorkTimePlan();

    /**
     * @param id
     * @return
     */
    TsWorkTimePlanEntity getActiveWorkTimePlanById(String id);

    /**
     * 获取最近要生效的版本
     *
     * @param id
     * @return
     */
    TsWorkTimePlanEntity getLatestToBeActiveVersionById(String id);

    /**
     * 获取最近要生效的版本
     *
     * @param uuid
     * @return
     */
    TsWorkTimePlanEntity getLatestToBeActiveVersionByUuid(String uuid);

    /**
     * @param uuid
     * @return
     */
    String getMaxVersionByUuid(String uuid);

    List<TsWorkTimePlanEntity> getAllBySystem(List<String> system);

    String getIdByUuid(String uuid);
}
