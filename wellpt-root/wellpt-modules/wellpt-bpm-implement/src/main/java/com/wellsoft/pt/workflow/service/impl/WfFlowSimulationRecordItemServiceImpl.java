/*
 * @(#)10/12/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.workflow.dao.WfFlowSimulationRecordItemDao;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationRecordItemEntity;
import com.wellsoft.pt.workflow.service.WfFlowSimulationRecordItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
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
 * 10/12/24.1	    zhulh		10/12/24		    Create
 * </pre>
 * @date 10/12/24
 */
@Service
public class WfFlowSimulationRecordItemServiceImpl extends AbstractJpaServiceImpl<WfFlowSimulationRecordItemEntity, WfFlowSimulationRecordItemDao, Long>
        implements WfFlowSimulationRecordItemService {

    /**
     * @param recordUuid
     * @return
     */
    @Override
    public List<WfFlowSimulationRecordItemEntity> listByRecordUuid(Long recordUuid) {
        if (recordUuid == null) {
            return Collections.emptyList();
        }

        WfFlowSimulationRecordItemEntity entity = new WfFlowSimulationRecordItemEntity();
        entity.setRecordUuid(recordUuid);
        return this.dao.listByEntity(entity);
    }

    /**
     * @param recordUuid
     */
    @Override
    @Transactional
    public void deleteByRecordUuid(Long recordUuid) {
        Assert.notNull(recordUuid, "仿真记录UUID不能为空！");

        String hql = "delete WfFlowSimulationRecordItemEntity t where t.recordUuid = :recordUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("recordUuid", recordUuid);
        this.dao.deleteByHQL(hql, params);
    }

}
