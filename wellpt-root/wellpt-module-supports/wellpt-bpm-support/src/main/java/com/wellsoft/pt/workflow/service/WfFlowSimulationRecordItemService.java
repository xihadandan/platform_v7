/*
 * @(#)10/12/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.dao.WfFlowSimulationRecordItemDao;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationRecordItemEntity;

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
 * 10/12/24.1	    zhulh		10/12/24		    Create
 * </pre>
 * @date 10/12/24
 */
public interface WfFlowSimulationRecordItemService extends JpaService<WfFlowSimulationRecordItemEntity, WfFlowSimulationRecordItemDao, Long> {

    /**
     * @param recordUuid
     * @return
     */
    List<WfFlowSimulationRecordItemEntity> listByRecordUuid(Long recordUuid);

    /**
     * @param recordUuid
     */
    void deleteByRecordUuid(Long recordUuid);

}
