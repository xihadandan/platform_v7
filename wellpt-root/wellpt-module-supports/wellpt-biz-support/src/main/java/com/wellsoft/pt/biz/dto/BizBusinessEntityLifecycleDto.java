/*
 * @(#)12/21/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.dto;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description: 项目生命周期数据传输类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/21/23.1	zhulh		12/21/23		Create
 * </pre>
 * @date 12/21/23
 */
@ApiModel("项目生命周期数据传输类")
public class BizBusinessEntityLifecycleDto extends BaseObject {
    private static final long serialVersionUID = 1002245766130319321L;

    @ApiModelProperty("办理过的业务流程定义")
    private List<ProcessDefinitionJson> processDefinitions = Lists.newArrayListWithCapacity(0);

    @ApiModelProperty("办理过的业务流程实例")
    private List<BizProcessInstanceDto> processInstances = Lists.newArrayListWithCapacity(0);

    @ApiModelProperty("办理过的过程节点实例")
    private List<BizProcessNodeInstanceDto> nodeInstances = Lists.newArrayListWithCapacity(0);

    @ApiModelProperty("办理过的业务事项实例")
    private List<BizProcessItemInstanceDto> itemInstances = Lists.newArrayListWithCapacity(0);

    /**
     * @return the processDefinitions
     */
    public List<ProcessDefinitionJson> getProcessDefinitions() {
        return processDefinitions;
    }

    /**
     * @param processDefinitions 要设置的processDefinitions
     */
    public void setProcessDefinitions(List<ProcessDefinitionJson> processDefinitions) {
        this.processDefinitions = processDefinitions;
    }

    /**
     * @return the processInstances
     */
    public List<BizProcessInstanceDto> getProcessInstances() {
        return processInstances;
    }

    /**
     * @param processInstances 要设置的processInstances
     */
    public void setProcessInstances(List<BizProcessInstanceDto> processInstances) {
        this.processInstances = processInstances;
    }

    /**
     * @return the nodeInstances
     */
    public List<BizProcessNodeInstanceDto> getNodeInstances() {
        return nodeInstances;
    }

    /**
     * @param nodeInstances 要设置的nodeInstances
     */
    public void setNodeInstances(List<BizProcessNodeInstanceDto> nodeInstances) {
        this.nodeInstances = nodeInstances;
    }

    /**
     * @return the itemInstances
     */
    public List<BizProcessItemInstanceDto> getItemInstances() {
        return itemInstances;
    }

    /**
     * @param itemInstances 要设置的itemInstances
     */
    public void setItemInstances(List<BizProcessItemInstanceDto> itemInstances) {
        this.itemInstances = itemInstances;
    }
}
