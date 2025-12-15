/*
 * @(#)10/14/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.dto;

import com.wellsoft.pt.workflow.entity.WfFlowSimulationRecordEntity;
import com.wellsoft.pt.workflow.entity.WfFlowSimulationRecordItemEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

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
 * 10/14/24.1	    zhulh		10/14/24		    Create
 * </pre>
 * @date 10/14/24
 */
@ApiModel("流程仿真记录")
public class WfFlowSimulationRecordDto extends WfFlowSimulationRecordEntity {

    @ApiModelProperty("流程仿真记录明细")
    private List<WfFlowSimulationRecordItemEntity> items;

    /**
     * @return the items
     */
    public List<WfFlowSimulationRecordItemEntity> getItems() {
        return items;
    }

    /**
     * @param items 要设置的items
     */
    public void setItems(List<WfFlowSimulationRecordItemEntity> items) {
        this.items = items;
    }
}
