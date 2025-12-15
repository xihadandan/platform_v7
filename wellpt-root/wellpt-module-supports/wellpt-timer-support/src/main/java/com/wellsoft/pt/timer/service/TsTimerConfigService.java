/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.timer.dao.TsTimerConfigDao;
import com.wellsoft.pt.timer.entity.TsTimerConfigEntity;
import com.wellsoft.pt.timer.support.TsTimerCategoryUsedChecker;

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
public interface TsTimerConfigService
        extends JpaService<TsTimerConfigEntity, TsTimerConfigDao, String>, TsTimerCategoryUsedChecker {

    /**
     * 配置计时器ID获取配置信息
     *
     * @param timerId
     * @return
     */
    TsTimerConfigEntity getById(String timerId);

    /**
     * 根据系统单位ID列表获取计时器配置
     *
     * @param systemUnitIds
     * @return
     */
    List<TsTimerConfigEntity> listBySystemUnitIds(List<String> systemUnitIds);

    /**
     * @param system
     * @param tenant
     * @param excludedCategoryId
     * @return
     */
    List<TsTimerConfigEntity> listBySystemAndTenant(String system, String tenant, String excludedCategoryId);

    /**
     * @param id
     * @return
     */
    long countById(String id);

}
