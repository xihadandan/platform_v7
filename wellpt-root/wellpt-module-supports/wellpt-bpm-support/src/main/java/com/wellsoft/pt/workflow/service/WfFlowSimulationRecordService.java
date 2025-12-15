/*
 * @(#)9/23/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.dao.WfFlowSimulationRecordDao;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationRecordEntity;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 9/23/24.1	    zhulh		9/23/24		    Create
 * </pre>
 * @date 9/23/24
 */
public interface WfFlowSimulationRecordService extends JpaService<WfFlowSimulationRecordEntity, WfFlowSimulationRecordDao, Long> {

    /**
     * @param flowInstUuid
     * @return
     */
    Long getUuidByFlowInstUuid(String flowInstUuid);

    /**
     * @param flowInstUuid
     * @return
     */
    WfFlowSimulationRecordEntity getByFlowInstUuid(String flowInstUuid);

    /**
     * @param flowDefUuid
     * @return
     */
    List<WfFlowSimulationRecordEntity> listByFlowDefUuid(String flowDefUuid);

    /**
     * @param uuid
     * @param state
     */
    void updateStateByUuid(Long uuid, String state);

    /**
     * @param uuids
     */
    void deleteAllByUuids(List<Long> uuids);

    /**
     * @param formUuid
     * @param dataUuid
     * @return
     */
    WfFlowSimulationRecordEntity getByFormUuidAndDataUuid(String formUuid, String dataUuid);

    /**
     * @param hql
     * @param values
     * @return
     */
    List<Long> listUuidByHQL(String hql, Map<String, Object> values);
}
