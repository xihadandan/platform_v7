/*
 * @(#)4/24/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.dao.WfFlowSettingDao;
import com.wellsoft.pt.workflow.entity.WfFlowSettingEntity;

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
 * 4/24/24.1	zhulh		4/24/24		Create
 * </pre>
 * @date 4/24/24
 */
public interface WfFlowSettingService extends JpaService<WfFlowSettingEntity, WfFlowSettingDao, Long> {
    /**
     * 根据系统及租户获取流程设置
     *
     * @param system
     * @param tenant
     * @return
     */
    List<WfFlowSettingEntity> listBySystemAndTenant(String system, String tenant);

    /**
     * @param attrKey
     * @param system
     * @param tenant
     * @return
     */
    WfFlowSettingEntity getByAttrKeyAndSystemAndTenant(String attrKey, String system, String tenant);

    /**
     * 获取流程设置
     *
     * @return
     */
    WorkFlowSettings getWorkFlowSettings();

    /**
     * 清除缓存
     */
    void clearFlowSettingsCache();

    /**
     * @param system
     * @param tenant
     * @return
     */
    long countBySystemAndTenant(String system, String tenant);

    /**
     * @return
     */
    List<String> listSystemIds();
}
