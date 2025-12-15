/*
 * @(#)10/12/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.support;

import com.google.common.collect.Lists;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationRecordItemEntity;

import java.util.List;

/**
 * Description: 流程仿真记录器
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
public class FlowSimulationRecorder {

    private List<WfFlowSimulationRecordItemEntity> items = Lists.newArrayList();

    /**
     * @param itemEntity
     */
    public void addItem(WfFlowSimulationRecordItemEntity itemEntity) {
        items.add(itemEntity);
    }

    /**
     * @return
     */
    public List<WfFlowSimulationRecordItemEntity> getItems() {
        return this.items;
    }

}
