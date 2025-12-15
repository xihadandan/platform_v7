/*
 * @(#)9/23/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 流程仿真设置
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
@ApiModel("流程仿真设置")
@Entity
@Table(name = "WF_FLOW_SIMULATION_SETTING")
@DynamicUpdate
@DynamicInsert
public class WfFlowSimulationSettingEntity extends com.wellsoft.context.jdbc.entity.Entity {
    private static final long serialVersionUID = 1861381728947802667L;

    @ApiModelProperty("流程定义UUID")
    private String flowDefUuid;

    @ApiModelProperty("仿真设置JSON信息")
    private String definitionJson;

    /**
     * @return the flowDefUuid
     */
    public String getFlowDefUuid() {
        return flowDefUuid;
    }

    /**
     * @param flowDefUuid 要设置的flowDefUuid
     */
    public void setFlowDefUuid(String flowDefUuid) {
        this.flowDefUuid = flowDefUuid;
    }

    /**
     * @return the definitionJson
     */
    public String getDefinitionJson() {
        return definitionJson;
    }

    /**
     * @param definitionJson 要设置的definitionJson
     */
    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

}
