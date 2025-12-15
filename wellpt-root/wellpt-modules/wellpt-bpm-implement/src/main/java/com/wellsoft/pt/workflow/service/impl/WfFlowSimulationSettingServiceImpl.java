/*
 * @(#)9/23/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.workflow.dao.WfFlowSimulationSettingDao;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationSettingEntity;
import com.wellsoft.pt.workflow.service.WfFlowSimulationSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
 * 9/23/24.1	    zhulh		9/23/24		    Create
 * </pre>
 * @date 9/23/24
 */
@Service
public class WfFlowSimulationSettingServiceImpl extends AbstractJpaServiceImpl<WfFlowSimulationSettingEntity, WfFlowSimulationSettingDao, Long>
        implements WfFlowSimulationSettingService {

    @Override
    public WfFlowSimulationSettingEntity getByFlowDefUuid(String flowDefUuid) {
        Assert.hasLength(flowDefUuid, "流程定义UUID不能为空！");

        WfFlowSimulationSettingEntity entity = new WfFlowSimulationSettingEntity();
        entity.setFlowDefUuid(flowDefUuid);
        List<WfFlowSimulationSettingEntity> entities = this.dao.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            return entities.get(0);
        }

        return null;
    }

}
